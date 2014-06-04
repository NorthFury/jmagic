package org.rnd.jmagic.engine;

import org.rnd.jmagic.CardLoader;
import org.rnd.jmagic.CardLoader.*;
import org.rnd.util.CompareOnToString;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Represents deck construction rules. */
public final class GameType
{
	@Target({ElementType.TYPE})
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface Ignore
	{
		// Marker annotation
	}

	/**
	 * Represents a single rule which might be combined with other rules to form
	 * a unique GameType. Each GameTypeRule must follow the JavaBean standards
	 * in order to allow dynamic introspection and construction.
	 */
	public static interface GameTypeRule
	{
		/**
		 * @param card The card to check
		 * @return Whether a card is allowed to be played with.
		 */
		public boolean checkCard(Class<? extends Card> card);

		/**
		 * @param expansion The expansion to check
		 * @return Whether cards from the expansion are allowed.
		 */
		public boolean checkExpansion(Expansion ex);

		/**
		 * @param deck The deck to check
		 * @return Whether the deck as a whole is allowed to be played with.
		 * @throws CardLoaderException
		 */
		public boolean checkDeck(Map<String, List<Class<? extends Card>>> deck);

		/**
		 * @return Decklists that are exempt from
		 * {@link GameTypeRule#checkCard(Class)}. For example, on 2011 July 1,
		 * Stoneforge Mystic was banned in Standard EXCEPT that the War of
		 * Attrition event decklist was legal if left completely intact.
		 */
		public Collection<Map<String, List<Class<? extends Card>>>> exemptDecklists();

		/**
		 * @return Whether this is a "base" card pool. Strictly for use by user
		 * interfaces; this has no meaning to the engine, and playing a
		 * "Standard Extended" game is technically possible.
		 */
		public boolean isBaseCardPool();

		/**
		 * Make any changes to the physical GameState before the game starts.
		 * 
		 * @param physicalState The physical GameState to change
		 */
		public void modifyGameState(GameState physicalState);
	}

	/**
	 * This is an implementation of GameTypeRule that implements each function
	 * and so you only have to override the functions you intend to use.
	 */
	public static class SimpleGameTypeRule implements GameTypeRule
	{
		/**
		 * @return true. See {@link GameTypeRule#checkCard(Class)}.
		 */
		@Override
		public boolean checkCard(Class<? extends Card> card)
		{
			return true;
		}

		/**
		 * @return true. See {@link GameTypeRule#checkExpansion}.
		 */
		@Override
		public boolean checkExpansion(Expansion ex)
		{
			return true;
		}

		/**
		 * @return true. See {@link GameTypeRule#checkDeck(Map)}.
		 * 
		 * @throws CardLoaderException
		 */
		@Override
		public boolean checkDeck(Map<String, List<Class<? extends Card>>> deck)
		{
			return true;
		}

		/**
		 * @return False.
		 */
		@Override
		public Collection<Map<String, List<Class<? extends Card>>>> exemptDecklists()
		{
			return Collections.emptyList();
		}

		/**
		 * @return false. See {@link GameTypeRule#isBaseCardPool()}.
		 */
		@Override
		public boolean isBaseCardPool()
		{
			return false;
		}

		/**
		 * Does nothing. See {@link GameTypeRule#modifyGameState(GameState)}.
		 */
		@Override
		public void modifyGameState(GameState physicalState)
		{
			// Do nothing
		}
	}

	private String name;
	private List<GameTypeRule> rules;
	private Map<String, Class<? extends Card>> cardpool;

	public GameType()
	{
		this(null);
	}

	public GameType(String name)
	{
		this.name = name;
		this.rules = new LinkedList<GameTypeRule>();
		this.cardpool = null;
	}

	public void addRule(GameTypeRule rule)
	{
		this.cardpool = null;
		this.rules.add(rule);
	}

	public PlayerInterface.ErrorParameters checkDeck(Map<String, List<Class<? extends Card>>> deck)
	{
		if(this.cardpool == null)
		{
			this.calculateCardPool();
		}

		for(GameTypeRule rule: this.rules)
			if(!rule.checkDeck(deck))
				return new PlayerInterface.ErrorParameters.DeckCheckError(rule.getClass());

		Class<? extends Card> cardBanned = null;
		for(List<Class<? extends Card>> deckPart: deck.values())
			for(Class<? extends Card> card: deckPart)
				if(!this.cardpool.containsKey(card.getAnnotation(Name.class).value()))
				{
					cardBanned = card;
					break;
				}

		if(cardBanned != null)
			for(GameTypeRule rule: this.rules)
				if(!checkExemptions(rule, deck))
				{
					cardBanned = null;
					break;
				}

		if(cardBanned != null)
			return new PlayerInterface.ErrorParameters.CardCheckError(cardBanned);

		return null;
	}

	private void calculateCardPool()
	{
		Set<Expansion> expansions = EnumSet.allOf(Expansion.class);
		for(GameTypeRule rule: this.rules)
		{
			Iterator<Expansion> iter = expansions.iterator();
			while(iter.hasNext())
				if(!rule.checkExpansion(iter.next()))
					iter.remove();
		}

		Set<Class<? extends Card>> cards = CardLoader.getCards(expansions);
		for(GameTypeRule rule: this.rules)
		{
			Iterator<Class<? extends Card>> iter = cards.iterator();
			while(iter.hasNext())
				if(!rule.checkCard(iter.next()))
					iter.remove();
		}

		this.cardpool = new HashMap<String, Class<? extends Card>>();

		for(Class<? extends Card> card: cards)
			this.cardpool.put(card.getAnnotation(Name.class).value(), card);
	}

	private static boolean checkExemptions(GameTypeRule rule, Map<String, List<Class<? extends Card>>> deck)
	{
		exemptions: for(Map<String, List<Class<? extends Card>>> exemptDeck: rule.exemptDecklists())
		{
			if(exemptDeck.size() != deck.size())
				continue;

			for(Map.Entry<String, List<Class<? extends Card>>> subDeck: deck.entrySet())
			{
				if(!exemptDeck.containsKey(subDeck.getKey()))
					continue exemptions;
				List<Class<? extends Card>> yourCardsUnsorted = subDeck.getValue();
				List<Class<? extends Card>> legalCardsUnsorted = exemptDeck.get(subDeck.getKey());
				if(legalCardsUnsorted.size() != yourCardsUnsorted.size())
					continue exemptions;

				List<Class<? extends Card>> yourCards = new ArrayList<Class<? extends Card>>(yourCardsUnsorted);
				Collections.sort(yourCards, new CompareOnToString<Class<? extends Card>>());

				List<Class<? extends Card>> legalCards = new ArrayList<Class<? extends Card>>(legalCardsUnsorted);
				Collections.sort(legalCards, new CompareOnToString<Class<? extends Card>>());

				if(!yourCards.equals(legalCards))
					continue exemptions;
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		GameType other = (GameType)obj;
		if(this.name == null)
		{
			if(other.name != null)
				return false;
		}
		else if(!this.name.equals(other.name))
			return false;
		return true;
	}

	public Map<String, Class<? extends Card>> getCardPool()
	{
		if(this.cardpool == null)
			this.calculateCardPool();
		return Collections.unmodifiableMap(this.cardpool);
	}

	public String getName()
	{
		return this.name;
	}

	public GameTypeRule[] getRules()
	{
		return this.rules.toArray(new GameTypeRule[0]);
	}

	public GameTypeRule getRules(int index)
	{
		return this.rules.get(index);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
		return result;
	}

	public void modifyGameState(GameState state)
	{
		for(GameTypeRule rule: this.rules)
			rule.modifyGameState(state);
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setRules(GameTypeRule[] rules)
	{
		this.rules.clear();
		this.rules.addAll(Arrays.asList(rules));
	}

	public void setRules(int index, GameTypeRule rule)
	{
		if(this.rules.size() <= index)
			throw new ArrayIndexOutOfBoundsException(index);
		this.rules.set(index, rule);
	}

	@Override
	public String toString()
	{
		return this.name;
	}
}
