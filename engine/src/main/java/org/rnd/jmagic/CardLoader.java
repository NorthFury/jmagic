package org.rnd.jmagic;

import org.rnd.jmagic.engine.*;
import org.rnd.util.CamelCase;
import org.rnd.util.SeparatedList;
import org.springframework.beans.factory.config.*;
import org.springframework.context.annotation.*;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.filter.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.annotation.AnnotationFormatError;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CardLoader extends URLClassLoader
{
	public static class CardLoaderException extends Exception
	{
		private static final long serialVersionUID = 1L;

		public final Set<String> cardNames;

		public CardLoaderException()
		{
			super("Unrecognized Cards");

			this.cardNames = new HashSet<String>();
		}

		public CardLoaderException(String cardName)
		{
			this();

			this.addCard(cardName);
		}

		public void addCard(String cardName)
		{
			this.cardNames.add(cardName);
		}

		public void combine(CardLoaderException ex)
		{
			this.cardNames.addAll(ex.cardNames);
		}

		public PlayerInterface.ErrorParameters.CardLoadingError getErrorParameters()
		{
			return new PlayerInterface.ErrorParameters.CardLoadingError(this.cardNames);
		}

		@Override
		public String getMessage()
		{
			return "Unrecognized Cards: " + SeparatedList.get("", this.cardNames);
		}
	}

	private static Pattern DECK_LINE_PATTERN;

	private static CardLoader _instance = new CardLoader();

	private static Map<String, Class<? extends Card>> cardClasses = new HashMap<String, Class<? extends Card>>();

	private static Map<String, String> NON_ASCII_REPLACE;

	static
	{
		NON_ASCII_REPLACE = new HashMap<String, String>();
		// Upper-case combined a-e
		NON_ASCII_REPLACE.put("\u00C6", "AE");
		// Lower-case e with an accent
		NON_ASCII_REPLACE.put("\u00E9", "e");
		// Lower-case o with an umlaut
		NON_ASCII_REPLACE.put("\u00F6", "o");
	}

	public static void addPackages(String... packages)
	{
		for(String pkg: packages)
			if(pkg != null)
				_instance.addPackage(pkg);
	}

	public static void addURLs(URL... urls)
	{
		for(URL url: urls)
			if(url != null)
				_instance.addURL(url);
	}

	/**
	 * Format a card name into a name understood by the engine
	 */
	public static String formatName(String name)
	{
		String ret = CamelCase.removeNonAlpha(name);
		for(Map.Entry<String, String> entry: NON_ASCII_REPLACE.entrySet())
			ret = ret.replace(entry.getKey(), entry.getValue());
		return ret;
	}

	public static Set<String> getAllCardNames()
	{
		return Collections.unmodifiableSet(cardClasses.keySet());
	}

	public static Set<Class<? extends Card>> getAllCards()
	{
		return getAllCards(false);
	}

	public static Set<Class<? extends Card>> getAllCards(boolean includeAlternateCards)
	{
		Set<Class<? extends Card>> ret = new HashSet<Class<? extends Card>>();

		for(Class<? extends Card> card: cardClasses.values())
			if(includeAlternateCards || !AlternateCard.class.isAssignableFrom(card))
				ret.add(card);

		return ret;
	}

	/**
	 * Find a card's class file by name. TODO: when we get around to using this
	 * for multiple interfaces, all the exception catching should either: a)
	 * throw a common error, or b) silently return null.
	 * 
	 * @param name The name of the card, no spaces, in camel-case
	 * @return The class object associated with that card
	 */
	public static Class<? extends Card> getCard(String name) throws CardLoaderException
	{
		if(!cardClasses.containsKey(name))
			throw new CardLoaderException(name);
		return cardClasses.get(name);
	}

	/**
	 * @param sets The sets to get cards from
	 * @return A set containing the class object of every card printed in at
	 * least one of the given sets
	 * @throws CardLoaderException
	 */
	public static Set<Class<? extends Card>> getCards(Collection<Expansion> sets)
	{
		return getCards(sets, false);
	}

	/**
	 * @param sets The sets to get cards from
	 * @return A set containing the class object of every card printed in at
	 * least one of the given sets
	 */
	public static Set<Class<? extends Card>> getCards(Collection<Expansion> sets, boolean includeAlternateCards)
	{
		Set<Class<? extends Card>> ret = new HashSet<Class<? extends Card>>();

		for(Class<? extends Card> c: getAllCards(includeAlternateCards))
		{
			Map<Expansion, Rarity> printingsArray = getPrintings(c);

			for(Expansion printing: printingsArray.keySet())
				if(sets.contains(printing))
				{
					ret.add(c);
					break;
				}
		}

		return ret;
	}

	public static int getCardsLoaded()
	{
		return _instance.cardsLoaded;
	}

	public static Deck getDeck(BufferedReader in) throws IOException
	{
		List<String> deck = new LinkedList<String>();
		List<String> sideboard = new LinkedList<String>();

		String line = in.readLine();
		while(null != line)
		{
			Matcher matcher = getDeckLinePattern().matcher(line);
			// The deck-line pattern matches every possible input, so it's a
			// major error if it doesn't match
			if(!matcher.matches())
				throw new RuntimeException("Regex that matches everything didn't match " + line);

			boolean mainDeck = (null == matcher.group(3));

			String numberString = matcher.group(5);
			int number;
			if(null == numberString)
				number = 1;
			else
				number = Integer.parseInt(numberString);

			String cardName = matcher.group(6);
			if(null != cardName)
			{
				if(mainDeck)
				{
					for(int i = 0; i < number; ++i)
						deck.add(cardName);
				}
				else
				{
					for(int i = 0; i < number; ++i)
						sideboard.add(cardName);
				}
			}

			line = in.readLine();
		}

		in.close();
		return new Deck(deck, sideboard);
	}

	/**
	 * Provide a pattern that matches every possible input but provides groups
	 * to get card information out of. The location of the card is in group 3,
	 * the number of cards is in group 5, and the name of the card is in group
	 * 6.
	 */
	private static Pattern getDeckLinePattern()
	{
		if(null == DECK_LINE_PATTERN)
			DECK_LINE_PATTERN = Pattern.compile("\\s*(((SB):\\s*)?((\\d+)\\s+)?([^#/]*[^#/\\s]))?\\s*(#.*)?(//.*)?");
		return DECK_LINE_PATTERN;
	}

	public static CardLoader getInstance()
	{
		return _instance;
	}

	public static Printings.Printed[] getPrintedAnnotation(Class<? extends Card> c)
	{
		if(!c.isAnnotationPresent(Printings.class))
			throw new AnnotationFormatError(c.getSimpleName() + " has no @Printed annotation");

		return c.getAnnotation(Printings.class).value();
	}

	public static SortedMap<Expansion, Rarity> getPrintings(Class<? extends Card> c)
	{
		SortedMap<Expansion, Rarity> ret = new TreeMap<Expansion, Rarity>();

		Printings.Printed[] printings = getPrintedAnnotation(c);
		for(Printings.Printed print: printings)
			ret.put(print.ex(), print.r());
		return ret;
	}

	public static Map<Rarity, Set<Class<? extends Card>>> getRarityMap(Expansion ex)
	{
		Map<Rarity, Set<Class<? extends Card>>> ret = new HashMap<Rarity, Set<Class<? extends Card>>>();

		for(Class<? extends Card> card: getCards(Collections.singleton(ex)))
		{
			Rarity rarity = null;
			for(Map.Entry<Expansion, Rarity> entry: getPrintings(card).entrySet())
				if(entry.getKey().equals(ex))
				{
					rarity = entry.getValue();
					break;
				}

			if(rarity == null)
				throw new RuntimeException("getCards(" + ex + ") returned a card such that getPrintings(card) didn't contain '" + ex + "'");

			if(!ret.containsKey(rarity))
				ret.put(rarity, new HashSet<Class<? extends Card>>());
			ret.get(rarity).add(card);
		}

		return ret;
	}

	public static void main(String[] args)
	{
		addPackages("org.rnd.jmagic.cards");

		for(Expansion ex: Expansion.values())
		{
			Collection<Class<? extends Card>> cards = getCards(Collections.singleton(ex));

			System.out.println(ex + ": \t" + cards.size());

			// for(Class<? extends Card> card: cards)
			// System.out.println("\t" + card.getSimpleName());
		}
	}

	private final Set<String> cardPackages;

	private int cardsLoaded;

	// Singleton constructor
	private CardLoader()
	{
		super(new URL[] {});

		this.cardPackages = new HashSet<String>();
		this.cardsLoaded = 0;
	}

	public void addPackage(String pkg)
	{
		if(this.cardPackages.add(pkg))
		{
			loadCardsFromClassLoader(Card.class.getClassLoader(), pkg);
			for(URL url: this.getURLs())
				loadCardsFromClassLoader(new URLClassLoader(new URL[] {url}), pkg);
		}
	}

	@Override
	public void addURL(URL url)
	{
		super.addURL(url);

		loadCardsFromURL(url);
	}

	@Override
	protected Class<?> findClass(String className) throws ClassNotFoundException
	{
		if(cardClasses.containsKey(className))
			return cardClasses.get(className);
		return super.findClass(className);
	}

	@SuppressWarnings("unchecked")
	private void loadCardsFromClassLoader(ClassLoader temploader, String pkg)
	{
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.setResourceLoader(new PathMatchingResourcePatternResolver(temploader));
		scanner.addIncludeFilter(new AssignableTypeFilter(Card.class));

		Set<BeanDefinition> components = scanner.findCandidateComponents(pkg);
		for(BeanDefinition bean: components)
		{
			try
			{
				Class<?> cls = temploader.loadClass(bean.getBeanClassName());

				String name = cls.getAnnotation(Name.class).value();

				List<String> additionalNames = new LinkedList<String>();
				for(Map.Entry<String, String> entry: NON_ASCII_REPLACE.entrySet())
					additionalNames.add(name.replace(entry.getKey(), entry.getValue()));

				if(bean instanceof ScannedGenericBeanDefinition && !((ScannedGenericBeanDefinition)bean).getMetadata().hasEnclosingClass())
				{
					this.resolveClass(cls);
					cardClasses.put(name, (Class<? extends Card>)cls);
					for(String additionalName: additionalNames)
						cardClasses.put(additionalName, (Class<? extends Card>)cls);
					++this.cardsLoaded;
				}
			}
			catch(ClassNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void loadCardsFromURL(URL url)
	{
		for(String pkg: this.cardPackages)
			loadCardsFromClassLoader(new URLClassLoader(new URL[] {url}), pkg);
	}
}
