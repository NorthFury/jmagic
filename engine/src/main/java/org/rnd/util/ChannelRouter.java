package org.rnd.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * <p>
 * Route data from channels to channels using only a single thread.
 * </p>
 * <p>
 * Use this class by calling
 * {@link #addRoute(SelectableChannel, SelectableChannel)} for each route, then
 * call {@link #run()} (either directly or in a {@link Thread}) to start the
 * routing. Route can be added while this is running, but it can only be run
 * once; the behavior of calling {@link #run()} again after it returns the first
 * time is undefined.
 * </p>
 * <p>
 * The motivation behind this class is behavior when using
 * {@link Channels#newInputStream(ReadableByteChannel)} and
 * {@link Channels#newOutputStream(WritableByteChannel)} on a channel: reading
 * from the resulting {@link InputStream} and writing to the resulting
 * {@link OutputStream} each synchronize on
 * {@link SelectableChannel#blockingLock()}. This means writing to the channel
 * can't be done while waiting on a read. This could be solved by using
 * non-blocking reads instead of blocking reads, but that may not be possible
 * (in the case of using {@link ObjectInputStream} on
 * {@link Channels#newInputStream(ReadableByteChannel)}). Instead, use
 * {@link Pipe} and this class together to allow a blocking read on the
 * {@link Pipe} while this class handles non-blocking reads and delivers data to
 * your {@link Pipe}.
 * </p>
 * <p>
 * This class contains a simple chat client/server example as a main method.
 * </p>
 */
public class ChannelRouter implements Runnable
{
	public static void main(String[] args) throws IOException
	{
		ChannelRouter channelRouter = new ChannelRouter(new ExceptionListener<IOException>()
		{
			@Override
			public void exceptionThrown(IOException exception)
			{
				exception.printStackTrace();
			}
		});

		int port = Integer.parseInt(args[1]);
		SocketChannel socketChannel;
		if(args[0].equals("--server"))
		{
			System.out.println("Server mode started; listening on port " + port);
			ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.socket().bind(new InetSocketAddress(port));
			socketChannel = serverSocketChannel.accept();
			System.out.println("Client connected");
			serverSocketChannel.close();
		}
		else
		{
			String host = args[0];
			System.out.println("Client mode started; connecting to " + host + " on port " + port);
			socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
			System.out.println("Connected to server");
		}
		socketChannel.configureBlocking(false);

		Pipe localToRemote = Pipe.open();
		localToRemote.source().configureBlocking(false);
		channelRouter.addRoute(localToRemote.source(), socketChannel);

		final Pipe remoteToLocal = Pipe.open();
		remoteToLocal.sink().configureBlocking(false);
		channelRouter.addRoute(socketChannel, remoteToLocal.sink());

		Thread channelRouterThread = new Thread(channelRouter, "ChannelRouter");
		channelRouterThread.start();

		Thread remoteReaderThread = new Thread("RemoteReader")
		{
			@Override
			public void run()
			{
				try
				{
					// BufferedReader.readLine() blocks until an end-of-line is
					// written, so make sure they get written by the main thread
					InputStream temporaryInputStream = Channels.newInputStream(remoteToLocal.source());
					BufferedReader in = new BufferedReader(new InputStreamReader(temporaryInputStream));
					String line = in.readLine();
					while(null != line)
					{
						System.out.println(line);
						line = in.readLine();
					}
				}
				catch(ClosedByInterruptException e)
				{
					// The main thread wants us to close, so do nothing
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		};
		remoteReaderThread.start();

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		Writer temporaryWriter = new OutputStreamWriter(Channels.newOutputStream(localToRemote.sink()));
		temporaryWriter = new BufferedWriter(temporaryWriter);
		PrintWriter out = new PrintWriter(temporaryWriter, true);

		System.out.println("Press end-of-file to terminate (Ctrl+Z on Windows, Ctrl+D on other platforms)");
		String line = in.readLine();
		while(null != line)
		{
			// The end-of-line is required for BufferedReader.readLine() to
			// return and this will automatically flush due to the auto-flush
			// parameter on construction
			out.println(line);
			line = in.readLine();
		}

		remoteReaderThread.interrupt();
		channelRouterThread.interrupt();
		socketChannel.close();
	}

	/**
	 * A convenience method for the following code:
	 * 
	 * <pre>
	 * {@link InputStream} tempInputStream = {@link Channels#newInputStream(ReadableByteChannel) Channels.newInputStream(input)};
	 * tempInputStream = new {@link BufferedInputStream#BufferedInputStream(InputStream) java.io.BufferedInputStream(tempInputStream)};
	 * return new {@link ObjectInputStream#ObjectInputStream(InputStream) java.io.ObjectInputStream(tempInputStream)};
	 * </pre>
	 * 
	 * @throws IOException pass-through for any
	 * {@link IOException} that occurs in the above code
	 */
	public static ObjectInputStream wrapChannelInObjectInputStream(ReadableByteChannel input) throws IOException
	{
		InputStream tempInputStream = Channels.newInputStream(input);
		tempInputStream = new BufferedInputStream(tempInputStream);
		return new ObjectInputStream(tempInputStream);
	}

	/**
	 * A convenience method for the following code:
	 * 
	 * <pre>
	 * {@link OutputStream} tempOutputStream = {@link Channels#newOutputStream(WritableByteChannel) Channels.newOutputStream(output)};
	 * tempOutputStream = new {@link BufferedOutputStream#BufferedOutputStream(OutputStream) java.io.BufferedOutputStream(tempOutputStream)};
	 * return new {@link ObjectOutputStream#ObjectOutputStream(OutputStream) java.io.ObjectOutputStream(tempOutputStream)};
	 * </pre>
	 * 
	 * @throws IOException pass-through for any
	 * {@link IOException} that occurs in the above code
	 */
	public static ObjectOutputStream wrapChannelInObjectOutputStream(WritableByteChannel output) throws IOException
	{
		OutputStream tempOutputStream = Channels.newOutputStream(output);
		tempOutputStream = new BufferedOutputStream(tempOutputStream);
		return new ObjectOutputStream(tempOutputStream);
	}

	private static int BUFFER_SIZE = 65536;

	private static int SELECT_MILLISECONDS = 100;

	private ExceptionListener<IOException> exceptionListener;

	private ConcurrentMap<SelectableChannel, ByteBuffer> outputBuffers;

	private ConcurrentMap<SelectableChannel, SelectableChannel> outputs;

	private Selector selector;

	public ChannelRouter(ExceptionListener<IOException> exceptionListener) throws IOException
	{
		this.exceptionListener = exceptionListener;
		this.outputBuffers = new ConcurrentHashMap<SelectableChannel, ByteBuffer>();
		this.outputs = new ConcurrentHashMap<SelectableChannel, SelectableChannel>();
		this.selector = Selector.open();
	}

	/**
	 * Add a route from one channel to another. An input can only have one
	 * output, but multiple inputs can be routed to a single output (no order is
	 * imposed, though, so this is usually not a good idea). The collection of
	 * routes is thread-safe, so it is legal to call this method after
	 * {@link #run()} has been called.
	 * 
	 * @param input This must be configured as non-blocking by using
	 * {@link SelectableChannel#configureBlocking(boolean)}
	 * @param output This must be configured as non-blocking in the same way as
	 * input
	 */
	public <T extends SelectableChannel & ReadableByteChannel, S extends SelectableChannel & WritableByteChannel> void addRoute(T input, S output) throws ClosedChannelException
	{
		// Must put into the maps before registering with the Selector in case
		// this thread is preempted by the input being available for read before
		// there's an output ready for it
		this.outputBuffers.put(output, ByteBuffer.allocate(BUFFER_SIZE));
		this.outputs.put(input, output);

		// Register the output channel with the selector if it isn't already
		// registered, but don't register for any operations yet; this will be
		// used later to switch the output to and from selecting for writing in
		// the run loop. In addition, do this first so the output key is always
		// available in case data is immediately available on input to be read
		if(!(output.isRegistered()))
			output.register(this.selector, 0);
		input.register(this.selector, SelectionKey.OP_READ);
	}

	/**
	 * A convenience method for the following code:
	 * 
	 * <pre>
	 * {@link Pipe} inputToOutput = {@link Pipe#open()};
	 * {@link Pipe.SourceChannel} source = {@link Pipe#source() inputToOutput.source()};
	 * {@link SelectableChannel#configureBlocking(boolean) source.configureBlocking(false)};
	 * {@link #addRoute(SelectableChannel, SelectableChannel) addRoute(source, output)};
	 * 
	 * {@link Pipe.SinkChannel} sink = {@link Pipe#sink() inputToOutput.sink()};
	 * return {@link #wrapChannelInObjectOutputStream(WritableByteChannel) wrapChannelInObjectOutputStream(sink)};
	 * </pre>
	 * 
	 * Users must call {@link ObjectOutputStream#flush()} before
	 * attempting to construct a matching {@link ObjectInputStream} to
	 * ensure a serialization header is available.
	 * 
	 * @throws IOException pass-through for any
	 * {@link IOException} that occurs in the above code
	 */
	public <T extends SelectableChannel & WritableByteChannel> ObjectOutputStream addRouteFromObjectOutputStream(T output) throws IOException
	{
		Pipe inputToOutput = Pipe.open();
		Pipe.SourceChannel source = inputToOutput.source();
		source.configureBlocking(false);
		addRoute(source, output);

		Pipe.SinkChannel sink = inputToOutput.sink();
		return wrapChannelInObjectOutputStream(sink);
	}

	/**
	 * A convenience method for the following code:
	 * 
	 * <pre>
	 * {@link Pipe} inputToOutput = {@link Pipe#open()};
	 * {@link Pipe.SinkChannel} sink = {@link Pipe#sink() inputToOutput.sink()};
	 * {@link SelectableChannel#configureBlocking(boolean) sink.configureBlocking(false)};
	 * {@link #addRoute(SelectableChannel, SelectableChannel) addRoute(input, sink)};
	 * 
	 * {@link Pipe.SourceChannel} source = {@link Pipe#source() inputToOutput.source()};
	 * return {@link #wrapChannelInObjectInputStream(ReadableByteChannel) wrapChannelInObjectInputStream(source)};
	 * </pre>
	 * 
	 * @throws IOException pass-through for any
	 * {@link IOException} that occurs in the above code
	 */
	public <T extends SelectableChannel & ReadableByteChannel> ObjectInputStream addRouteToObjectInputStream(T input) throws IOException
	{
		Pipe inputToOutput = Pipe.open();
		Pipe.SinkChannel sink = inputToOutput.sink();
		sink.configureBlocking(false);
		addRoute(input, sink);

		Pipe.SourceChannel source = inputToOutput.source();
		return wrapChannelInObjectInputStream(source);
	}

	private void closeChannelAndReportException(Channel channel)
	{
		try
		{
			channel.close();
		}
		catch(IOException e)
		{
			reportIOException(e);
		}
	}

	private void handleReadableChannel(SelectionKey key, SelectableChannel channel)
	{
		try
		{
			if(!key.isReadable())
				return;

			SelectableChannel output = this.outputs.get(channel);
			ByteBuffer buffer = this.outputBuffers.get(output);

			if(-1 == ((ReadableByteChannel)channel).read(buffer))
				// This has never happened as read has always thrown an
				// exception instead, but handle it just in case
				throw new EOFException();

			SelectionKey outputKey = output.keyFor(this.selector);
			// The channel might have been closed and unregistered by the time
			// this happened
			if(null != outputKey)
				outputKey.interestOps(outputKey.interestOps() | SelectionKey.OP_WRITE);

			// The code below the catch blocks below is for error-handling only
			return;
		}
		catch(CancelledKeyException e)
		{
			// Error-handling code below to avoid code duplication
		}
		catch(ClosedChannelException e)
		{
			// Error-handling code below to avoid code duplication
		}
		catch(EOFException e)
		{
			// Error-handling code below to avoid code duplication
		}
		catch(IOException e)
		{
			reportIOException(e);

			// Continue with the error-handling code below
		}

		this.outputs.remove(channel);

		try
		{
			// No longer interested in handling this channel for read operations
			key.interestOps(key.interestOps() & ~SelectionKey.OP_READ);
		}
		catch(CancelledKeyException e)
		{
			// Do nothing
		}

		// The channel should already be closed, but just in case
		closeChannelAndReportException(channel);
	}

	private void handleWritableChannel(SelectionKey key, SelectableChannel channel)
	{
		try
		{
			if(!key.isWritable())
				return;

			ByteBuffer buffer = this.outputBuffers.get(channel);
			buffer.flip();
			((WritableByteChannel)channel).write(buffer);

			// If there aren't any bytes left to write from the buffer, tell the
			// selector that we no longer want to write to the channel (and-not
			// disables only that bit)
			if(0 == buffer.remaining())
				key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
			buffer.compact();

			// The code below the catch blocks below is for error-handling only
			return;
		}
		catch(CancelledKeyException e)
		{
			// Error-handling code below to avoid code duplication
		}
		catch(ClosedChannelException e)
		{
			// Error-handling code below to avoid code duplication
		}
		catch(EOFException e)
		{
			// Error-handling code below to avoid code duplication
		}
		catch(IOException e)
		{
			reportIOException(e);

			// Continue with the error-handling code below
		}

		this.outputBuffers.remove(channel);

		// Close and remove any routes that point to this channel as an output
		Iterator<Map.Entry<SelectableChannel, SelectableChannel>> i = this.outputs.entrySet().iterator();
		while(i.hasNext())
		{
			Map.Entry<SelectableChannel, SelectableChannel> entry = i.next();
			if(entry.getValue().equals(channel))
			{
				closeChannelAndReportException(entry.getKey());
				i.remove();
			}
		}

		try
		{
			// No longer interested in handling this channel for write
			// operations
			key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
		}
		catch(CancelledKeyException e)
		{
			// Do nothing
		}

		// The channel should already be closed, but just in case
		closeChannelAndReportException(channel);
	}

	private void reportIOException(IOException e)
	{
		if(null != this.exceptionListener)
			this.exceptionListener.exceptionThrown(e);
	}

	@Override
	public void run()
	{
		try
		{
			while(true)
			{
				this.selector.select(SELECT_MILLISECONDS);
				if(Thread.interrupted())
					break;

				Iterator<SelectionKey> i = this.selector.selectedKeys().iterator();
				while(i.hasNext())
				{
					SelectionKey key = i.next();
					SelectableChannel channel = key.channel();
					handleReadableChannel(key, channel);
					handleWritableChannel(key, channel);
					i.remove();
				}
			}
		}
		catch(ClosedByInterruptException e)
		{
			// User-requested interrupt, so clean up
		}
		catch(IOException e)
		{
			reportIOException(e);
		}

		for(Map.Entry<SelectableChannel, SelectableChannel> e: this.outputs.entrySet())
		{
			closeChannelAndReportException(e.getKey());
			closeChannelAndReportException(e.getValue());
		}

		for(SelectableChannel c: this.outputBuffers.keySet())
			closeChannelAndReportException(c);
	}
}
