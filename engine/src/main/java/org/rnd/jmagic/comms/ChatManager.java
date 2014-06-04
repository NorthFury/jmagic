package org.rnd.jmagic.comms;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatManager
{
	public interface Callback
	{
		public void gotMessage(String message);
	}

	public interface MessagePoster
	{
		public void postMessage(String message);
	}

	private List<Callback> clients = new CopyOnWriteArrayList<Callback>();

	public MessagePoster addClient(final String playerName, Callback callback)
	{
		this.clients.add(callback);
		return new MessagePoster()
		{
			@Override
			public void postMessage(String message)
			{
				String fullMessage = playerName + ": " + message;
				for(Callback c: ChatManager.this.clients)
					c.gotMessage(fullMessage);
			}
		};
	}
}
