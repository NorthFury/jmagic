package org.rnd.jmagic.comms;

import org.rnd.jmagic.Version;
import org.rnd.jmagic.engine.Game;
import org.rnd.jmagic.engine.GameType;
import org.rnd.jmagic.engine.Player;
import org.rnd.jmagic.engine.PlayerInterface;
import org.rnd.util.ChannelRouter;
import org.rnd.util.ExceptionListener;
import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;
import org.teleal.cling.support.igd.PortMappingListener;
import org.teleal.cling.support.model.PortMapping;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.channels.Channel;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * A reference implementation for playing jMagic over a socket. After
 * constructing the object, call
 * {@link #addLocalPlayer(PlayerInterface)} for each
 * player which will be playing within the same VM as the server.
 * </p>
 * <p>
 * For players connecting via socket, all communication is via serialized
 * objects created by {@link ObjectOutputStream} as follows:
 * </p>
 * <ol>
 * <li>Write the serialization stream header by calling the constructor
 * {@link ObjectOutputStream#ObjectOutputStream(java.io.OutputStream)}</li>
 * <li>Write a {@link Version} representing the version of the
 * client using {@link ObjectOutputStream#writeObject(Object)}</li>
 * <li>Write a {@link Server.SocketType} representing the type of socket using
 * {@link ObjectOutputStream#writeObject(Object)}</li>
 * <li>Flush the connection to make sure the server receives it by calling
 * {@link ObjectOutputStream#flush()}</li>
 * <li>Read a {@link Version} using
 * {@link ObjectInputStream#readObject()} to verify a version match or
 * mismatch</li>
 * </ol>
 */
public class Server implements Runnable
{
	/**
	 * These are the only socket types recognized by {@link Server}.
	 */
	public static enum SocketType
	{
		/**
		 * This must be the first socket connected. After verifying version
		 * compatibility, the client must read a {@link UUID} using
		 * {@link ObjectInputStream#readObject()} which serves as a key
		 * for any other sockets. All traffic afterwards must be handled using
		 * {@link StreamPlayer}.
		 */
		ENGINE,
		/**
		 * This must only be connected after receiving a key from the
		 * {@link #ENGINE} socket. Immediately after writing {@link #CHAT} in
		 * the protocol described by {@link Server}, the client must write the
		 * key to the socket using
		 * {@link ObjectOutputStream#writeObject(Object)}. After
		 * receiving a {@link Version} from the server, the
		 * client must continually call
		 * {@link ObjectInputStream#readUTF()} to receive chat messages
		 * from all players (including the player connected by this socket)
		 * prefixed by the name of that player.
		 */
		CHAT
	}

	private static final int HEARTBEAT_MILLISECONDS = 15000;

	private static final Logger LOG = Logger.getLogger(Server.class.getName());

	private ChannelRouter channelRouter;

	private List<Channel> channels;

	private ChatManager chatManager;

	private List<Thread> chatThreads;

	private Thread connectionThread = null;

	private Game game;

	private String gameDescription = null;

	private GameFinder gameFinder = null;

	private GameType gameType;

	private String hostPlayerName = null;

	private ConcurrentMap<UUID, Player> players;

	private int playerLimit;

	private int port;

	private boolean readyToRun = true;

	private Selector selector = null;

	private ServerSocketChannel serverChannel = null;

	private UpnpService upnpService = null;

	private Version version;

	public Server(GameType gameType, int numPlayers, int port)
	{
		try
		{
			this.channelRouter = new ChannelRouter(new ExceptionListener<IOException>()
			{
				@Override
				public void exceptionThrown(IOException exception)
				{
					LOG.log(Level.SEVERE, "IO error while routing across channels", exception);
				}
			});
		}
		catch(IOException e)
		{
			LOG.log(Level.SEVERE, "IO error while setting up channel router", e);
		}

		this.channels = new LinkedList<Channel>();

		this.chatManager = new ChatManager();

		this.chatThreads = new LinkedList<Thread>();

		this.game = new Game(gameType);

		if(null == gameType)
		{
			LOG.severe("Must provide a game type");
			this.readyToRun = false;
		}
		this.gameType = gameType;

		if(numPlayers < 1)
		{
			LOG.severe("Number of players must be greater than 0");
			this.readyToRun = false;
		}
		this.playerLimit = numPlayers;

		this.players = new ConcurrentHashMap<UUID, Player>();

		if((port < 0) || (65535 < port))
		{
			LOG.severe("Port must be between 0 and 65535");
			this.readyToRun = false;
		}
		this.port = port;

		this.version = new Version();
	}

	private boolean acceptConnections()
	{
		final Object connectionLock = new Object();
		final AtomicBoolean error = new AtomicBoolean(false);

		this.connectionThread = new Thread("AcceptConnections")
		{
			@Override
			public void run()
			{
				while(true)
				{
					int numKeys = 0;
					try
					{
						numKeys = Server.this.selector.select(HEARTBEAT_MILLISECONDS);
					}
					catch(ClosedSelectorException e)
					{
						// The main thread is telling us to stop, so gracefully
						// exit
						return;
					}
					catch(IOException e)
					{
						error.set(true);
						synchronized(connectionLock)
						{
							connectionLock.notify();
						}
						LOG.log(Level.WARNING, "IO error while selecting", e);
						return;
					}

					if(0 < numKeys)
					{
						Server.this.selector.selectedKeys().clear();
						acceptOneConnection();
						synchronized(connectionLock)
						{
							connectionLock.notify();
						}
					}
					else if(Thread.currentThread().isInterrupted())
					{
						// We can only be interrupted by the server thread, so
						// no need to set the error flag
						LOG.fine("Interrupting listening for clients due to user interrupt");
						return;
					}
					else
					{
						LOG.fine("Heartbeat period timeout; sending heartbeat");
						heartbeatGameFinder();
					}
				}
			}
		};

		try
		{
			synchronized(connectionLock)
			{
				this.connectionThread.start();
				while(this.players.size() < this.playerLimit)
				{
					connectionLock.wait(500);
					if(error.get())
						return false;
				}
				return true;
			}
		}
		catch(InterruptedException e)
		{
			this.connectionThread.interrupt();
		}

		return false;
	}

	private void acceptOneConnection()
	{
		try
		{
			LOG.info("Accepting a connection");
			SocketChannel channel = this.serverChannel.accept();
			channel.configureBlocking(false);
			this.channels.add(channel);

			// Always create the output stream first and flush it before
			// creating the input stream so a stream header is immediately
			// available
			ObjectOutputStream out = this.channelRouter.addRouteFromObjectOutputStream(channel);
			out.writeObject(this.version);
			out.flush();

			ObjectInputStream in = this.channelRouter.addRouteToObjectInputStream(channel);

			// Read a Version object from the other side to see if we're
			// compatible
			Version otherVersion = (Version)in.readObject();
			if(!this.version.isCompatibleWith(otherVersion))
			{
				LOG.log(Level.INFO, "The player attempted to connect with a non-compatible version (local: " + this.version + ", remote: " + otherVersion + "); rejecting player");
				return;
			}

			SocketType header = (SocketType)(in.readObject());

			switch(header)
			{
			case ENGINE:
				if(this.players.size() == this.playerLimit)
				{
					LOG.info("Too many players are trying to connect");
					channel.close();
					return;
				}

				// Generate a random key for the player to use for future
				// connections
				UUID newKey = UUID.randomUUID();

				out.writeObject(newKey);
				out.flush();

				PlayerInterface playerInterface = new StreamPlayer(in, out);
				Player player = this.game.addInterface(playerInterface);
				if(null == player)
				{
					LOG.info("Player " + playerInterface.getName() + " deck error; rejecting player");
					channel.close();
					return;
				}

				this.channels.add(channel);
				this.players.put(newKey, player);

				LOG.info("Player " + player.getName() + " connected with key " + newKey);
				updateGameFinder();
				break;

			case CHAT:
				UUID oldKey = (UUID)(in.readObject());
				if(!this.players.containsKey(oldKey))
				{
					LOG.info("Player with key " + oldKey + " does not exist in players map");
					channel.close();
					return;
				}

				final String playerName = this.players.get(oldKey).getName();
				this.channels.add(channel);

				StreamChatterServer chatter = new StreamChatterServer(in, out, new ExceptionListener<IOException>()
				{
					@Override
					public void exceptionThrown(IOException exception)
					{
						LOG.log(Level.WARNING, "IO error for player " + playerName, exception);
					}
				});
				chatter.setMessagePoster(this.chatManager.addClient(playerName, chatter));
				Thread chatThread = new Thread(chatter, "ChatInputFromSocketFor" + oldKey);
				this.chatThreads.add(chatThread);
				chatThread.start();
				break;
			}
		}
		catch(ClassCastException e)
		{
			LOG.log(Level.INFO, "A connection attempt was made without following the protocol", e);
		}
		catch(ClassNotFoundException e)
		{
			LOG.log(Level.INFO, "The player sent an unrecognized class; rejecting player", e);
		}
		catch(IOException e)
		{
			LOG.log(Level.INFO, "An IO error occured during the player connecting; rejecting player", e);
		}
		catch(Game.InterruptedGameException e)
		{
			LOG.fine("Interrupting listening for clients due to user interrupt");
		}
	}

	public ChatManager.MessagePoster addLocalPlayer(PlayerInterface playerInterface, ChatManager.Callback chatCallback)
	{
		Player player = this.game.addInterface(playerInterface);
		if(null == player)
		{
			// Don't log anything as any errors are passed to
			// playerInterface.alertError which is logged elsewhere
			return null;
		}

		String playerName = player.getName();
		if(null == this.hostPlayerName)
			this.hostPlayerName = playerName;
		// Generate a fake key for the local player
		this.players.put(UUID.randomUUID(), player);
		return this.chatManager.addClient(playerName, chatCallback);
	}

	private void alertHostError()
	{
		try
		{
			for(Player p: this.game.actualState.players)
				p.comm.alertError(new PlayerInterface.ErrorParameters.HostError());
		}
		// We have to catch RuntimeException here because alertError has no
		// other way to indicate an error
		catch(RuntimeException e)
		{
			LOG.log(Level.SEVERE, "Error while alerting players to the game error", e);
		}
	}

	private void cancelGameFinder()
	{
		try
		{
			if(null != this.gameFinder)
			{
				LOG.info("Cancelling game from game-finder");
				this.gameFinder.cancel();
			}
		}
		catch(IOException e)
		{
			LOG.log(Level.WARNING, "Error cancelling game on game-finder; state of game might not be accurate on game-finder", e);
		}
	}

	private void closeConnections()
	{
		this.connectionThread.interrupt();

		for(Thread t: this.chatThreads)
			t.interrupt();

		try
		{
			this.serverChannel.close();
			this.selector.close();
		}
		catch(IOException e)
		{
			LOG.log(Level.WARNING, "IO error while cleaning up", e);
		}

		for(Channel c: this.channels)
		{
			try
			{
				c.close();
			}
			catch(IOException e)
			{
				LOG.log(Level.WARNING, "IO error while closing connection", e);
			}
		}
	}

	private void forwardUPNP()
	{
		try
		{
			String localHostIP = InetAddress.getLocalHost().getHostAddress();
			String description = "jMagic at " + localHostIP + ":" + this.port;
			PortMapping portMapping;
			portMapping = new PortMapping(this.port, localHostIP, PortMapping.Protocol.TCP, description);
			// No game should last more than a day, right?
			portMapping.setLeaseDurationSeconds(new UnsignedIntegerFourBytes(86400));
			this.upnpService = new UpnpServiceImpl(new PortMappingListener(portMapping));
			this.upnpService.getControlPoint().search();
		}
		catch(UnknownHostException e)
		{
			LOG.log(Level.WARNING, "Could not look up local IP address", e);
		}
	}

	private void heartbeatGameFinder()
	{
		try
		{
			if(null != this.gameFinder)
				this.gameFinder.heartbeat();
		}
		catch(IOException e)
		{
			LOG.log(Level.WARNING, "Error heartbeating game on game-finder; game may be removed from game-finder", e);
		}
	}

	private boolean listenForConnections()
	{
		try
		{
			this.serverChannel = ServerSocketChannel.open();
			this.serverChannel.configureBlocking(false);
			this.serverChannel.socket().bind(new InetSocketAddress(this.port));
			LOG.info("Listening for connections on port " + this.port);

			this.selector = Selector.open();
			this.serverChannel.register(this.selector, SelectionKey.OP_ACCEPT);

			return true;
		}
		catch(IOException e)
		{
			LOG.log(Level.SEVERE, "Could not open port to listen for connections", e);
			return false;
		}
	}

	private void registerWithGameFinder()
	{
		try
		{
			if(null == this.gameFinder)
				return;

			this.gameFinder.create(this.hostPlayerName, this.port, this.gameDescription, this.playerLimit, this.gameType.getName());
			LOG.info("Game registered with game-finder");
		}
		catch(IOException e)
		{
			LOG.log(Level.WARNING, "Error reading from game-finder; not using game-finder", e);
			this.gameFinder = null;
		}
		catch(GameFinder.GameFinderException e)
		{
			LOG.warning(e.getMessage() + "; not using game-finder");
			this.gameFinder = null;
		}
	}

	@Override
	public void run()
	{
		if(!this.readyToRun)
			return;

		Thread routingThread = new Thread(this.channelRouter, "ChannelRouter");
		routingThread.start();

		if(setup())
		{
			runGame();
			closeConnections();
			stopForwardUPNP();
		}

		routingThread.interrupt();
	}

	private void runGame()
	{
		if(Thread.currentThread().isInterrupted())
			return;

		try
		{
			Player winner = this.game.run();
			LOG.info("Game completed successfully with winner " + winner);
		}
		catch(Game.InterruptedGameException e)
		{
			LOG.fine("Host thread interrupted");
			alertHostError();
		}
		catch(RuntimeException e)
		{
			LOG.log(Level.SEVERE, "Error while hosting the game", e);
			alertHostError();
		}
	}

	private boolean setup()
	{
		boolean ret = false;

		if(listenForConnections())
		{
			forwardUPNP();
			registerWithGameFinder();

			if(acceptConnections())
				ret = true;
			else
			{
				cancelGameFinder();
				closeConnections();
				stopForwardUPNP();
			}
		}

		return ret;
	}

	private void stopForwardUPNP()
	{
		if(null != this.upnpService)
			this.upnpService.shutdown();
	}

	private void updateGameFinder()
	{
		try
		{
			if(null != this.gameFinder)
			{
				LOG.info("Updating game-finder updated with new player");
				this.gameFinder.update();
			}
		}
		catch(IOException e)
		{
			LOG.log(Level.WARNING, "Error updating game-finder; state of game might not be accurate on game-finder", e);
		}
	}

	/**
	 * At least one local player must be added before running the {@link Server}
	 */
	public void useGameFinder(String url, String description)
	{
		try
		{
			this.gameDescription = description;

			this.gameFinder = new GameFinder(url);
		}
		catch(URISyntaxException e)
		{
			LOG.log(Level.WARNING, "Can't understand game-finder URL; not using game-finder", e);
		}
		catch(IllegalArgumentException e)
		{
			LOG.log(Level.WARNING, "Game-finder URL is not absolute; not using game-finder", e);
		}
		catch(MalformedURLException e)
		{
			LOG.log(Level.WARNING, "Game-finder URL is malformed; not using game-finder", e);
		}
		catch(IOException e)
		{
			LOG.log(Level.WARNING, "Error reading from game-finder; not using game-finder", e);
		}
		catch(GameFinder.GameFinderException e)
		{
			LOG.warning(e.getMessage() + "; not using game-finder");
		}
	}
}
