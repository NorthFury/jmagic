package org.rnd.jmagic.engine;

import org.rnd.jmagic.CardLoader;
import org.rnd.jmagic.CardLoader.*;
import org.rnd.jmagic.Convenience;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Deck implements Serializable
{
	private static final long serialVersionUID = 1L;

	public static final String MAIN_DECK = "MAIN_DECK";
	public static final String SIDEBOARD = "SIDEBOARD";

	protected final Map<String, List<String>> cards;
	public final Map<String, List<String>> publicCardMap;
	protected transient Map<String, List<Class<? extends Card>>> classMap;

	public Deck()
	{
		this.cards = new HashMap<String, List<String>>();
		this.publicCardMap = Collections.unmodifiableMap(this.cards);
		this.cards.put(MAIN_DECK, new LinkedList<String>());
		this.cards.put(SIDEBOARD, new LinkedList<String>());
		this.classMap = null;
	}

	public Deck(List<String> mainDeck, List<String> sideboard)
	{
		this();
		this.cards.get(MAIN_DECK).addAll(mainDeck);
		this.cards.get(SIDEBOARD).addAll(sideboard);
	}

	public Deck(Class<? extends Card>... mainDeck)
	{
		this();
		List<String> main = this.cards.get(MAIN_DECK);
		for(Class<? extends Card> cls: mainDeck)
			main.add(Convenience.getName(cls));
	}

	public Map<String, List<Class<? extends Card>>> getCards() throws CardLoader.CardLoaderException
	{
		if(this.classMap == null)
		{
			CardLoader.CardLoaderException failAfter = null;

			Map<String, List<Class<? extends Card>>> ret = new HashMap<String, List<Class<? extends Card>>>();

			for(Map.Entry<String, List<String>> entry: this.cards.entrySet())
			{
				List<Class<? extends Card>> clsList = new LinkedList<Class<? extends Card>>();
				ret.put(entry.getKey(), clsList);

				for(String cardName: entry.getValue())
				{
					Class<? extends Card> cls = null;
					try
					{
						cls = CardLoader.getCard(cardName);
					}
					catch(CardLoaderException e)
					{
						if(failAfter == null)
							failAfter = e;
						else
							failAfter.combine(e);
					}
					clsList.add(cls);
				}
			}

			if(null != failAfter)
				throw failAfter;

			this.classMap = ret;
		}

		return this.classMap;
	}
}
