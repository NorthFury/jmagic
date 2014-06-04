package org.rnd.jmagic.engine.gameTypes;

import org.rnd.jmagic.CardLoader.*;
import org.rnd.jmagic.engine.*;

import java.util.List;
import java.util.Map;

/**
 * 100.2a. In constructed play (a way of playing in which each player creates
 * his or her own deck ahead of time), each deck must contain at least sixty
 * cards. A constructed deck may contain any number of basic land cards and no
 * more than four of any card with a particular English name other than basic
 * land cards.
 */
@Name("Deck size minimum")
@Description("The minimum number of cards that must be in your main deck (excluding any sideboard cards or similar)")
public class DeckSizeMinimum extends GameType.SimpleGameTypeRule
{
	private int minimum;

	public DeckSizeMinimum()
	{
		this.minimum = 0;
	}

	public DeckSizeMinimum(int minimum)
	{
		this.minimum = minimum;
	}

	/**
	 * @return Whether the given deck conforms to the rules for constructed
	 * play.
	 * @throws CardLoaderException
	 */
	@Override
	public boolean checkDeck(Map<String, List<Class<? extends Card>>> deck)
	{
		return (this.minimum <= deck.get(Deck.MAIN_DECK).size());
	}

	public int getMinimum()
	{
		return this.minimum;
	}

	public void setMinimum(int minimum)
	{
		this.minimum = minimum;
	}
}
