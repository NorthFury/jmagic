package org.rnd.jmagic.comms;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.sanitized.*;
import org.rnd.util.NumberRange;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.channels.ClosedByInterruptException;
import java.util.List;

public class StreamPlayer implements PlayerInterface
{
	private enum Function
	{
		ALERT_CHOICE
		{
			@Override
			public void run(ObjectInputStream in, ObjectOutputStream out, PlayerInterface local) throws IOException, ClassNotFoundException
			{
				int playerID = in.readInt();
				ChooseParameters<?> choice = (ChooseParameters<?>)in.readObject();
				local.alertChoice(playerID, choice);
			}
		},
		ALERT_ERROR
		{
			@Override
			public void run(ObjectInputStream in, ObjectOutputStream out, PlayerInterface local) throws IOException, ClassNotFoundException
			{
				local.alertError((ErrorParameters)in.readObject());
				Thread.currentThread().interrupt();
			}
		},
		ALERT_EVENT
		{
			@Override
			public void run(ObjectInputStream in, ObjectOutputStream out, PlayerInterface local) throws IOException, ClassNotFoundException
			{
				local.alertEvent((SanitizedEvent)in.readObject());
			}
		},
		ALERT_STATE
		{
			@Override
			public void run(ObjectInputStream in, ObjectOutputStream out, PlayerInterface local) throws IOException, ClassNotFoundException
			{
				local.alertState((SanitizedGameState)in.readObject());
			}
		},
		ALERT_STATE_REVERSION
		{
			@Override
			public void run(ObjectInputStream in, ObjectOutputStream out, PlayerInterface local) throws IOException, ClassNotFoundException
			{
				local.alertStateReversion((PlayerInterface.ReversionParameters)in.readObject());
			}
		},
		ALERT_WAITING
		{
			@Override
			public void run(ObjectInputStream in, ObjectOutputStream out, PlayerInterface local) throws IOException, ClassNotFoundException
			{
				local.alertWaiting((SanitizedPlayer)in.readObject());
			}
		},
		CHOOSE
		{
			@Override
			public void run(ObjectInputStream in, ObjectOutputStream out, PlayerInterface local) throws IOException, ClassNotFoundException
			{
				out.writeObject(local.choose((ChooseParameters<?>)in.readObject()));
				out.reset();
				out.flush();
			}
		},
		CHOOSE_NUMBER
		{
			@Override
			public void run(ObjectInputStream in, ObjectOutputStream out, PlayerInterface local) throws IOException, ClassNotFoundException
			{
				NumberRange range = (NumberRange)in.readObject();
				String description = in.readUTF();
				out.writeInt(local.chooseNumber(range, description));
				out.reset();
				out.flush();
			}
		},
		DIVIDE
		{
			@Override
			public void run(ObjectInputStream in, ObjectOutputStream out, PlayerInterface local) throws IOException, ClassNotFoundException
			{
				int quantity = in.readInt();
				int minimum = in.readInt();
				int whatFrom = in.readInt();
				String beingDivided = in.readUTF();
				@SuppressWarnings("unchecked") List<SanitizedTarget> targets = (List<SanitizedTarget>)in.readObject();
				local.divide(quantity, minimum, whatFrom, beingDivided, targets);
				out.writeObject(targets);
				out.reset();
				out.flush();
			}
		},
		GET_DECK
		{
			@Override
			public void run(ObjectInputStream in, ObjectOutputStream out, PlayerInterface local) throws IOException
			{
				out.writeObject(local.getDeck());
				out.reset();
				out.flush();
			}
		},
		GET_NAME
		{
			@Override
			public void run(ObjectInputStream in, ObjectOutputStream out, PlayerInterface local) throws IOException
			{
				out.writeUTF(local.getName());
				out.reset();
				out.flush();
			}
		},
		SET_PLAYER_ID
		{
			@Override
			public void run(ObjectInputStream in, ObjectOutputStream out, PlayerInterface local) throws IOException
			{
				local.setPlayerID(in.readInt());
			}
		};

		public abstract void run(ObjectInputStream in, ObjectOutputStream out, PlayerInterface local) throws IOException, ClassNotFoundException;
	}

	/**
	 * Run a local interface using commands from outputStream and sending
	 * responses to inputStream.
	 * <p>
	 * The protocol is very simple: perform a version check against the other
	 * side, then, for each pass in an infinite loop, read a Function (using an
	 * internal ObjectInputStream) from inputStream and call the appropriate
	 * function on local, writing (using an internal ObjectOutputStream) any
	 * return value to outputStream.
	 * <p>
	 * This may sound similar to the RMI protocol and that's because I used RMI
	 * as a basis for this. The advantages to this are the ability to easily use
	 * any in/out streams to communicate, which means potentially multiple users
	 * connecting over a single port on the server.
	 * 
	 * @param in The input stream to read (must be connected to the output
	 * stream of a StreamPlayer instance)
	 * @param out The output stream to write (must be connected to the input
	 * stream of a StreamPlayer instance)
	 * @param local The PlayerInterface to call functions on
	 */
	public static void run(ObjectInputStream in, ObjectOutputStream out, PlayerInterface local) throws ClassNotFoundException, IOException
	{
		try
		{
			while(true)
				((Function)in.readObject()).run(in, out, local);
		}
		catch(ClosedByInterruptException e)
		{
			throw new Game.InterruptedGameException();
		}
	}

	private ObjectInputStream in;

	private ObjectOutputStream out;

	public StreamPlayer(ObjectInputStream in, ObjectOutputStream out)
	{
		this.in = in;
		this.out = out;
	}

	@Override
	public void alertChoice(int playerID, ChooseParameters<?> choice)
	{
		try
		{
			this.out.writeObject(Function.ALERT_CHOICE);
			this.out.writeInt(playerID);
			this.out.writeObject(choice);
			this.out.reset();
			this.out.flush();
		}
		catch(ClosedByInterruptException e)
		{
			throw new Game.InterruptedGameException();
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void alertError(ErrorParameters parameters)
	{
		try
		{
			this.out.writeObject(Function.ALERT_ERROR);
			this.out.writeObject(parameters);
			this.out.reset();
			this.out.flush();
		}
		catch(ClosedByInterruptException e)
		{
			throw new Game.InterruptedGameException();
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void alertEvent(SanitizedEvent event)
	{
		try
		{
			this.out.writeObject(Function.ALERT_EVENT);
			this.out.writeObject(event);
			this.out.reset();
			this.out.flush();
		}
		catch(ClosedByInterruptException e)
		{
			throw new Game.InterruptedGameException();
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void alertState(SanitizedGameState sanitizedGameState)
	{
		try
		{
			this.out.writeObject(Function.ALERT_STATE);
			this.out.writeObject(sanitizedGameState);
			this.out.reset();
			this.out.flush();
		}
		catch(ClosedByInterruptException e)
		{
			throw new Game.InterruptedGameException();
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void alertStateReversion(PlayerInterface.ReversionParameters parameters)
	{
		try
		{
			this.out.writeObject(Function.ALERT_STATE_REVERSION);
			this.out.writeObject(parameters);
			this.out.reset();
			this.out.flush();
		}
		catch(ClosedByInterruptException e)
		{
			throw new Game.InterruptedGameException();
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void alertWaiting(SanitizedPlayer who)
	{
		try
		{
			this.out.writeObject(Function.ALERT_WAITING);
			this.out.writeObject(who);
			this.out.reset();
			this.out.flush();
		}
		catch(ClosedByInterruptException e)
		{
			throw new Game.InterruptedGameException();
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Serializable> List<Integer> choose(ChooseParameters<T> parameterObject)
	{
		try
		{
			this.out.writeObject(Function.CHOOSE);
			this.out.writeObject(parameterObject);
			this.out.reset();
			this.out.flush();

			return (List<Integer>)this.in.readObject();
		}
		catch(ClosedByInterruptException e)
		{
			throw new Game.InterruptedGameException();
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
		catch(ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public int chooseNumber(NumberRange range, String description)
	{
		try
		{
			this.out.writeObject(Function.CHOOSE_NUMBER);
			this.out.writeObject(range);
			this.out.writeUTF(description);
			this.out.reset();
			this.out.flush();

			return this.in.readInt();
		}
		catch(ClosedByInterruptException e)
		{
			throw new Game.InterruptedGameException();
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void divide(int quantity, int minimum, int whatFrom, String beingDivided, List<SanitizedTarget> targets)
	{
		try
		{
			this.out.writeObject(Function.DIVIDE);
			this.out.writeInt(quantity);
			this.out.writeInt(minimum);
			this.out.writeInt(whatFrom);
			this.out.writeUTF(beingDivided);
			this.out.writeObject(targets);
			this.out.reset();
			this.out.flush();

			@SuppressWarnings("unchecked") List<SanitizedTarget> divisions = (List<SanitizedTarget>)this.in.readObject();
			for(int i = 0; i < targets.size(); ++i)
				targets.get(i).division = divisions.get(i).division;
		}
		catch(ClosedByInterruptException e)
		{
			throw new Game.InterruptedGameException();
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
		catch(ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public Deck getDeck()
	{
		try
		{
			this.out.writeObject(Function.GET_DECK);
			this.out.reset();
			this.out.flush();

			return (Deck)this.in.readObject();
		}
		catch(ClosedByInterruptException e)
		{
			throw new Game.InterruptedGameException();
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
		catch(ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getName()
	{
		try
		{
			this.out.writeObject(Function.GET_NAME);
			this.out.reset();
			this.out.flush();

			return this.in.readUTF();
		}
		catch(ClosedByInterruptException e)
		{
			throw new Game.InterruptedGameException();
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setPlayerID(int playerID)
	{
		try
		{
			this.out.writeObject(Function.SET_PLAYER_ID);
			this.out.writeInt(playerID);
			this.out.reset();
			this.out.flush();
		}
		catch(ClosedByInterruptException e)
		{
			throw new Game.InterruptedGameException();
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
