package org.rnd.jmagic.engine.gameTypes;

import org.rnd.jmagic.engine.*;

import java.util.List;
import java.util.Map;

@Name("Deck size maximum")
@Description("The maximum number of cards that must be in your main deck (excluding any sideboard cards or similar)")
public class DeckSizeMaximum extends GameType.SimpleGameTypeRule
{
	private int maximum;

	public DeckSizeMaximum()
	{
		this.maximum = 0;
	}

	public DeckSizeMaximum(int maximum)
	{
		this.maximum = maximum;
	}

	@Override
	public boolean checkDeck(Map<String, List<Class<? extends Card>>> deck)
	{
		return (deck.get(Deck.MAIN_DECK).size() <= this.maximum);
	}

	public int getMaximum()
	{
		return this.maximum;
	}

	public void setMaximum(int maximum)
	{
		this.maximum = maximum;
	}
}
