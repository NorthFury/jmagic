package org.rnd.jmagic.engine.gameTypes.packWars;

import org.rnd.jmagic.CardLoader;
import org.rnd.jmagic.CardLoader.*;
import org.rnd.jmagic.engine.*;
import org.rnd.util.Constructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Name("Specific cards")
public class SpecificCardsBoosterFactory implements BoosterFactory
{
	private List<String> cardNames;

	public SpecificCardsBoosterFactory()
	{
		this.cardNames = new ArrayList<String>();
	}

	public SpecificCardsBoosterFactory(String... cardNames)
	{
		this();
		this.setCardNames(cardNames);
	}

	public String[] getCardNames()
	{
		return this.cardNames.toArray(new String[this.cardNames.size()]);
	}

	public String getCardNames(int index)
	{
		return this.cardNames.get(index);
	}

	public void setCardNames(String[] cardNames)
	{
		this.cardNames = Arrays.asList(cardNames);
	}

	public void setCardNames(int index, String cardName)
	{
		while(index >= this.cardNames.size())
			this.cardNames.add(null);
		this.cardNames.set(index, cardName);
	}

	@Override
	public List<Card> createBooster(GameState state) throws CardLoaderException
	{
		List<Card> ret = new LinkedList<Card>();

		for(String name: this.cardNames)
		{
			Class<? extends Card> clazz = CardLoader.getCard(name);
			Card instance = Constructor.construct((Class<? extends Card>)clazz, new Class<?>[] {GameState.class}, new Object[] {state});
			instance.setExpansionSymbol(clazz.getAnnotation(Printings.class).value()[0].ex());
			ret.add(instance);
		}

		return ret;
	}

}
