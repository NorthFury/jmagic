package org.rnd.jmagic.gui;

import org.rnd.jmagic.comms.ChatManager;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.gui.dialogs.ConfigurationFrame;
import org.rnd.jmagic.interfaceAdapters.*;
import org.rnd.jmagic.sanitized.*;
import org.rnd.util.*;

import javax.swing.SwingUtilities;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

public class SwingAdapter implements ConfigurableInterface
{
	private final SortedSet<ConfigurationFrame.OptionPanel> options;

	private Play gui;
	private Deck deck;
	private String name;

	public SwingAdapter(Deck deck, String name)
	{
		this.options = new TreeSet<ConfigurationFrame.OptionPanel>(new Comparator<ConfigurationFrame.OptionPanel>()
		{
			@Override
			public int compare(ConfigurationFrame.OptionPanel o1, ConfigurationFrame.OptionPanel o2)
			{
				return o1.getName().compareTo(o2.getName());
			}
		});

		this.gui = new Play(this.getOptions());

		this.deck = deck;
		this.name = name;
	}

	@Override
	public void alertChoice(final int playerID, final ChooseParameters<?> choice)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				SwingAdapter.this.gui.alertChoice(playerID, choice);
			}
		});
	}

	@Override
	public void alertError(final ErrorParameters parameters)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				SwingAdapter.this.gui.alertError(parameters);
			}
		});
	}

	@Override
	public void alertEvent(final SanitizedEvent event)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				SwingAdapter.this.gui.alertEvent(event);
			}
		});
	}

	@Override
	public void alertState(final SanitizedGameState sanitizedGameState)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				SwingAdapter.this.gui.alertState(sanitizedGameState);
			}
		});
	}

	@Override
	public void alertStateReversion(final PlayerInterface.ReversionParameters parameters)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				SwingAdapter.this.gui.alertStateReversion(parameters);
			}
		});
	}

	@Override
	public void alertWaiting(final SanitizedPlayer who)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				SwingAdapter.this.gui.alertWaiting(who);
			}
		});
	}

	@Override
	public <T extends Serializable> List<Integer> choose(final ChooseParameters<T> parameterObject)
	{
		synchronized(this.gui)
		{
			this.gui.choiceReady = false;
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					synchronized(SwingAdapter.this.gui)
					{
						SwingAdapter.this.gui.choose(parameterObject);
					}
				}
			});
			try
			{
				while(!this.gui.choiceReady)
					this.gui.wait();
			}
			catch(InterruptedException e)
			{
				throw new Game.InterruptedGameException();
			}
			return this.gui.choose;
		}
	}

	@Override
	public int chooseNumber(final NumberRange range, final String description)
	{
		synchronized(this.gui)
		{
			this.gui.choiceReady = false;
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					synchronized(SwingAdapter.this.gui)
					{
						SwingAdapter.this.gui.chooseNumber(range, description);
					}
				}
			});
			try
			{
				while(!this.gui.choiceReady)
					this.gui.wait();
			}
			catch(InterruptedException e)
			{
				throw new Game.InterruptedGameException();
			}
			return this.gui.chooseNumber;
		}
	}

	@Override
	public void divide(final int quantity, final int minimum, final int whatFrom, final String beingDivided, final List<SanitizedTarget> targets)
	{
		synchronized(this.gui)
		{
			this.gui.choiceReady = false;
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					synchronized(SwingAdapter.this.gui)
					{
						SwingAdapter.this.gui.divide(quantity, minimum, whatFrom, beingDivided, targets);
					}
				}
			});
			try
			{
				while(!this.gui.choiceReady)
					this.gui.wait();
			}
			catch(InterruptedException e)
			{
				throw new Game.InterruptedGameException();
			}
		}
	}

	public ChatManager.Callback getChatCallback()
	{
		return this.gui.getChatCallback();
	}

	@Override
	public Deck getDeck()
	{
		return this.deck;
	}

	@Override
	public String getName()
	{
		return this.name;
	}

	@Override
	public ConfigurationFrame.OptionPanel getOptionPanel()
	{
		return null;
	}

	@Override
	public SortedSet<ConfigurationFrame.OptionPanel> getOptions()
	{
		return this.options;
	}

	public void setMessagePoster(ChatManager.MessagePoster messagePoster)
	{
		this.gui.setMessagePoster(messagePoster);
	}

	@Override
	public void setPlayerID(final int playerID)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				SwingAdapter.this.gui.setPlayerID(playerID);
			}
		});
	}

	@Override
	public void setProperties(final Properties properties)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				SwingAdapter.this.gui.setProperties(properties);
			}
		});
	}
}
