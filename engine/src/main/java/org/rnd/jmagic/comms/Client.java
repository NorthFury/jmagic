package org.rnd.jmagic.comms;

import org.rnd.jmagic.Version;
import org.rnd.jmagic.engine.Game;
import org.rnd.jmagic.engine.PlayerInterface;
import org.rnd.util.ChannelRouter;
import org.rnd.util.ExceptionListener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.SocketChannel;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * A reference implementation for playing jMagic over a socket. This is designed
 * to be used with the {@link Server} class on the other end of the sockets.
 */
public class Client implements Runnable
{
	private static final Logger LOG = Logger.getLogger(Client.class.getName());

	private ChannelRouter channelRouter;

	private String host;

	private String name;

	private PlayerInterface playerInterface;

	private int port;

	private StreamChatterClient streamChatterClient;

	private Version version;

	public Client(String host, int port, String name, PlayerInterface playerInterface, ChatManager.Callback chatCallback) throws IOException
	{
		this.channelRouter = new ChannelRouter(new ExceptionListener<IOException>()
		{
			@Override
			public void exceptionThrown(IOException exception)
			{
				LOG.log(Level.SEVERE, "I/O error while running ChannelRouter", exception);
			}
		});

		this.host = host;
		this.name = name;
		this.playerInterface = playerInterface;
		this.port = port;

		this.streamChatterClient = new StreamChatterClient(chatCallback, new ExceptionListener<IOException>()
		{
			@Override
			public void exceptionThrown(IOException exception)
			{
				LOG.log(Level.SEVERE, "IOException while reading from System.in or writing to server", exception);
			}
		});

		this.version = new Version();
	}

	private void connectChat(UUID key)
	{
		try
		{
			SocketChannel channel = SocketChannel.open(new InetSocketAddress(this.host, this.port));
			channel.configureBlocking(false);

			// Always create the output stream first and flush it before
			// creating the input stream so a stream header is immediately
			// available
			ObjectOutputStream out = this.channelRouter.addRouteFromObjectOutputStream(channel);
			out.writeObject(this.version);
			out.writeObject(Server.SocketType.CHAT);
			out.writeObject(key);
			out.flush();

			ObjectInputStream in = this.channelRouter.addRouteToObjectInputStream(channel);

			// Read a Version object from the other side to see if we're
			// compatible
			Version otherVersion = (Version)in.readObject();
			if(!this.version.isCompatibleWith(otherVersion))
			{
				LOG.severe("Version mismatch between client (" + this.version + ") and host(" + otherVersion + ")");
				return;
			}

			this.streamChatterClient.setStreams(in, out);
			this.streamChatterClient.run();
		}
		catch(UnknownHostException e)
		{
			LOG.severe("Could not find host \"" + this.host + "\"");
		}
		catch(ClosedByInterruptException e)
		{
			LOG.fine("Interrupting chat connection due to user interrupt");
		}
		catch(IOException e)
		{
			LOG.log(Level.SEVERE, "I/O error while trying to connect", e);
		}
		catch(ClassNotFoundException e)
		{
			LOG.log(Level.SEVERE, "Server is not speaking using expected protocol", e);
		}
	}

	public ChatManager.MessagePoster getMessagePoster()
	{
		return this.streamChatterClient;
	}

	@Override
	public void run()
	{
		LOG.info("Trying to connect to " + this.host + " on port " + this.port);

		Thread routingThread = new Thread(this.channelRouter, "ChannelRouter");
		routingThread.start();

		SocketChannel channel = null;
		Thread chatInputThread = null;

		try
		{
			channel = SocketChannel.open(new InetSocketAddress(this.host, this.port));
			channel.configureBlocking(false);

			// Always create the output stream first and flush it before
			// creating the input stream so a stream header is immediately
			// available
			ObjectOutputStream out = this.channelRouter.addRouteFromObjectOutputStream(channel);
			out.writeObject(this.version);
			out.writeObject(Server.SocketType.ENGINE);
			out.flush();

			ObjectInputStream in = this.channelRouter.addRouteToObjectInputStream(channel);

			// Read a Version object from the other side to see if we're
			// compatible
			Version otherVersion = (Version)in.readObject();
			if(!this.version.isCompatibleWith(otherVersion))
			{
				LOG.severe("Version mismatch between client (" + this.version + ") and host(" + otherVersion + ")");
				routingThread.interrupt();
				return;
			}

			final UUID key = (UUID)(in.readObject());

			LOG.info("Starting game as player \"" + this.name + "\"");

			chatInputThread = new Thread("ChatInputFromSocket")
			{
				@Override
				public void run()
				{
					connectChat(key);
				}
			};
			chatInputThread.start();

			StreamPlayer.run(in, out, this.playerInterface);
		}
		catch(UnknownHostException e)
		{
			LOG.severe("Could not find host \"" + this.host + "\"");
		}
		catch(ClassNotFoundException e)
		{
			LOG.log(Level.SEVERE, "The server sent an unknown class-type", e);
		}
		catch(ClosedByInterruptException e)
		{
			LOG.fine("Interrupting opening connection due to user interrupt");
		}
		catch(IOException e)
		{
			LOG.log(Level.SEVERE, "An IO error occured while communicating with the server", e);
		}
		catch(Game.InterruptedGameException e)
		{
			// The interface will throw this exception if it wants to end the
			// game prematurely, so just exit
			LOG.fine("Ending jMagic due to user interrupt");
		}

		try
		{
			if(null != channel)
				channel.close();
		}
		catch(IOException e)
		{
			LOG.log(Level.WARNING, "IOException when closing socket", e);
		}

		LOG.fine("Ending jMagic");
		if(null != chatInputThread)
			chatInputThread.interrupt();
		routingThread.interrupt();
	}
}
