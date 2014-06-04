package org.rnd.jmagic.engine.gameTypes.packWars;

import org.rnd.jmagic.*;
import org.rnd.jmagic.CardLoader.CardLoaderException;
import org.rnd.jmagic.engine.*;
import org.rnd.util.Constructor;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Name("Full expansion")
public class FullExpansionBoosterFactory implements BoosterFactory
{
	private List<Expansion> expansions;

	public FullExpansionBoosterFactory()
	{
		this.expansions = new LinkedList<Expansion>();
	}

	public FullExpansionBoosterFactory(Expansion expansion)
	{
		this.expansions = new LinkedList<Expansion>();
		this.expansions.add(expansion);
	}

	@Override
	public List<Card> createBooster(GameState state) throws CardLoaderException
	{
		List<Card> ret = new LinkedList<Card>();

		for(Expansion expansion: this.expansions)
			for(Class<? extends Card> cardClass: CardLoader.getCards(Arrays.asList(expansion)))
			{
				Card card = Constructor.construct(cardClass, new Class<?>[] {GameState.class}, new Object[] {state});
				card.setExpansionSymbol(expansion);
				ret.add(card);
			}

		return ret;
	}

	public Expansion[] getExpansions()
	{
		return this.expansions.toArray(new Expansion[this.expansions.size()]);
	}

	public Expansion getExpansions(int index)
	{
		return this.expansions.get(index);
	}

	public void setExpansions(Expansion[] expansions)
	{
		this.expansions = Arrays.asList(expansions);
	}

	public void setExpansions(int index, Expansion expansion)
	{
		while(this.expansions.size() <= index)
			this.expansions.add(null);
		this.expansions.set(index, expansion);
	}
}
