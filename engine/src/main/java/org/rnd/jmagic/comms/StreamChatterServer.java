package org.rnd.jmagic.comms;

import org.rnd.util.ExceptionListener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.String;

public class StreamChatterServer implements ChatManager.Callback, Runnable
{
	private ObjectInputStream in;

	private ExceptionListener<IOException> ioExceptionListener;

	private ChatManager.MessagePoster messagePoster;

	private ObjectOutputStream out;

	public StreamChatterServer(ObjectInputStream in, ObjectOutputStream out, ExceptionListener<IOException> ioExceptionListener)
	{
		this.in = in;
		this.ioExceptionListener = ioExceptionListener;
		this.out = out;
	}

	@Override
	public void gotMessage(String message)
	{
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
		if(null == this.messagePoster)
			return;

		try
		{
			while(true)
				this.messagePoster.postMessage(this.in.readUTF());
		}
		catch(IOException e)
		{
			this.ioExceptionListener.exceptionThrown(e);
		}
	}

	public void setMessagePoster(ChatManager.MessagePoster messagePoster)
	{
		this.messagePoster = messagePoster;
	}
}
