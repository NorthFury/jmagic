package org.rnd.jmagic.engine.gameTypes.packWars;

import org.rnd.jmagic.*;
import org.rnd.jmagic.CardLoader.CardLoaderException;
import org.rnd.jmagic.engine.*;
import org.rnd.util.Constructor;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Name("Expansion booster")
public class ExpansionBoosterFactory implements BoosterFactory
{
	private static class CardClassWithExpansion
	{
		public final Class<? extends Card> cardClass;
		public final Expansion expansion;

		public CardClassWithExpansion(String name, Expansion expansion) throws CardLoaderException
		{
			this.cardClass = CardLoader.getCard(name);
			this.expansion = expansion;
		}

		public CardClassWithExpansion(Class<? extends Card> cardClass, Expansion expansion)
		{
			this.cardClass = cardClass;
			this.expansion = expansion;
		}
	}

	private static int addRandomCards(Collection<Card> cards, List<CardClassWithExpansion> possible, int num, GameState state)
	{
		int added = 0;
		Collections.shuffle(possible);

		for(int i = 0; (i < num) && (i < possible.size()); ++i)
		{
			CardClassWithExpansion cardClassWithExpansion = possible.remove(0);
			Card card = Constructor.construct(cardClassWithExpansion.cardClass, new Class<?>[] {GameState.class}, new Object[] {state});
			card.setExpansionSymbol(cardClassWithExpansion.expansion);
			cards.add(card);
			++added;
		}

		return added;
	}

	private List<Expansion> expansions;
	private boolean rarityIgnored;

	public ExpansionBoosterFactory()
	{
		this.expansions = new LinkedList<Expansion>();
		this.rarityIgnored = false;
	}

	public ExpansionBoosterFactory(Expansion expansion)
	{
		this.expansions = new LinkedList<Expansion>();
		this.expansions.add(expansion);
		this.rarityIgnored = false;
	}

	@Override
	public List<Card> createBooster(GameState state) throws CardLoaderException
	{
		List<Card> ret = new LinkedList<Card>();

		if(this.rarityIgnored)
		{
			List<CardClassWithExpansion> allCards = new LinkedList<CardClassWithExpansion>();
			for(Expansion expansion: this.expansions)
				for(Class<? extends Card> cardClass: CardLoader.getCards(Arrays.asList(expansion)))
					allCards.add(new CardClassWithExpansion(cardClass, expansion));
			addRandomCards(ret, allCards, 14, state);
		}
		else
		{
			Map<Rarity, List<CardClassWithExpansion>> allCards = new HashMap<Rarity, List<CardClassWithExpansion>>();
			for(Rarity rarity: Rarity.values())
				allCards.put(rarity, new LinkedList<CardClassWithExpansion>());

			for(Expansion expansion: this.expansions)
			{
				for(Map.Entry<Rarity, Set<Class<? extends Card>>> e: CardLoader.getRarityMap(expansion).entrySet())
				{
					List<CardClassWithExpansion> rarityCards = allCards.get(e.getKey());
					for(Class<? extends Card> cardClass: e.getValue())
						rarityCards.add(new CardClassWithExpansion(cardClass, expansion));
				}
			}

			int mythicsAdded = 0;

			// Only include a mythic in one in eight packs (this can result in 0
			// included mythics even if the probability works out if there
			// aren't any mythics in this expansion)
			if(Math.random() < 0.125)
				mythicsAdded = addRandomCards(ret, allCards.get(Rarity.MYTHIC), 1, state);

			// Only include a rare if a mythic wasn't included
			if(0 == mythicsAdded)
				addRandomCards(ret, allCards.get(Rarity.RARE), 1, state);

			addRandomCards(ret, allCards.get(Rarity.UNCOMMON), 10, state);
			addRandomCards(ret, allCards.get(Rarity.COMMON), 3, state);
		}

		// Always include one land (not all sets have lands, so always take it
		// from the latest base set)
		List<CardClassWithExpansion> lands = new LinkedList<CardClassWithExpansion>();
		lands.add(new CardClassWithExpansion("Plains", Expansion.MAGIC_2011));
		lands.add(new CardClassWithExpansion("Island", Expansion.MAGIC_2011));
		lands.add(new CardClassWithExpansion("Swamp", Expansion.MAGIC_2011));
		lands.add(new CardClassWithExpansion("Mountain", Expansion.MAGIC_2011));
		lands.add(new CardClassWithExpansion("Forest", Expansion.MAGIC_2011));
		addRandomCards(ret, lands, 1, state);

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

	public boolean isRarityIgnored()
	{
		return this.rarityIgnored;
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

	public void setRarityIgnored(boolean ignoreRarity)
	{
		this.rarityIgnored = ignoreRarity;
	}
}
