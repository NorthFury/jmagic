package org.rnd.jmagic.comms;

import org.rnd.util.ExceptionListener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.String;
import java.nio.channels.ClosedByInterruptException;

public class StreamChatterClient implements ChatManager.MessagePoster, Runnable
{
	private ChatManager.Callback callback;

	private ObjectInputStream in;

	private ExceptionListener<IOException> ioExceptionListener;

	private ObjectOutputStream out;

	public StreamChatterClient(ChatManager.Callback callback, ExceptionListener<IOException> ioExceptionListener)
	{
		this.callback = callback;
		this.in = null;
		this.ioExceptionListener = ioExceptionListener;
		this.out = null;
	}

	@Override
	public void postMessage(String message)
	{
		if(null == this.out)
			return;

		try
		{
			this.out.writeUTF(message);
			this.out.flush();
		}
		catch(IOException e)
		{
			this.ioExceptionListener.exceptionThrown(e);
		}
	}

	@Override
	public void run()
	{
		if(null == this.in)
			return;

		try
		{
			while(true)
				this.callback.gotMessage(this.in.readUTF());
		}
		catch(ClosedByInterruptException e)
		{
			// The user requested this, so just exit
		}
		catch(IOException e)
		{
			this.ioExceptionListener.exceptionThrown(e);
		}
	}

	public void setStreams(ObjectInputStream in, ObjectOutputStream out)
	{
		this.in = in;
		this.out = out;
	}
}
