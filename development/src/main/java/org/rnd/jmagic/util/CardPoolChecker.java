package org.rnd.jmagic.util;

import org.rnd.jmagic.CardLoader;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.interfaceAdapters.SimplePlayerInterface;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CardPoolChecker
{
	private static void checkFile(final File file, GameType type, final boolean delete) throws IOException
	{
		Game game = new Game(type);
		final Deck deck = CardLoader.getDeck(new BufferedReader(new FileReader(file)));

		game.addInterface(new SimplePlayerInterface(null)
		{
			@Override
			public void alertError(ErrorParameters parameters)
			{
				// This is where we should get information about whether the
				// deck is legal or not
				if(parameters instanceof PlayerInterface.ErrorParameters.CardLoadingError)
					System.err.println(file + " could not load cards " + ((PlayerInterface.ErrorParameters.CardLoadingError)parameters).cardNames);
				else if(parameters instanceof PlayerInterface.ErrorParameters.DeckCheckError)
					System.err.println(file + " failed deck check: " + ((PlayerInterface.ErrorParameters.DeckCheckError)parameters).rule);
				else if(parameters instanceof PlayerInterface.ErrorParameters.CardCheckError)
					System.err.println(file + " failed card check: " + ((PlayerInterface.ErrorParameters.CardCheckError)parameters).card);
				else if(parameters instanceof PlayerInterface.ErrorParameters.IllegalCardsError)
					System.err.println(file + " contained illegal cards " + ((PlayerInterface.ErrorParameters.IllegalCardsError)parameters).cardNames);
				else
					System.err.println("Unknown error type " + parameters.getClass());

				if(delete)
					file.delete();
			}

			@Override
			public Deck getDeck()
			{
				return deck;
			}

			@Override
			public String getName()
			{
				// This interface exists only to return a deck
				return "CardPoolChecker";
			}

			@Override
			public void setPlayerID(int playerID)
			{
				// This interface exists only to return a deck
			}
		});
	}

	private static void checkPath(File path, String potentialPool, boolean delete) throws IOException
	{
		if(path.isDirectory())
		{
			String[] parts = path.getName().split(" ");
			for(File sub: path.listFiles())
				if(!sub.getAbsolutePath().endsWith(".svn") && !sub.getAbsolutePath().endsWith("_svn"))
					checkPath(sub, parts[0], delete);
		}
		else
		{
			GameType type = null;
			if(potentialPool.equals("Block"))
				type = GameTypes.BLOCK;
			else if(potentialPool.equals("Standard"))
				type = GameTypes.STANDARD;
			else if(potentialPool.equals("Extended"))
				type = GameTypes.EXTENDED;
			else if(potentialPool.equals("Modern"))
				type = GameTypes.MODERN;
			else if(potentialPool.equals("Legacy"))
				type = GameTypes.LEGACY;
			else if(potentialPool.equals("Vintage"))
				type = GameTypes.VINTAGE;
			else
				return;
			checkFile(path, type, delete);
		}
	}

	public static void main(String[] args) throws IOException
	{
		System.out.println("Loading cards");
		CardLoader.addPackages("org.rnd.jmagic.cards");
		System.out.println("Done loading cards");
		System.out.println();
		System.out.println("Checking decks");
		checkPath(new File("./misc information/decks/"), null, args.length > 0 && args[0].equals("delete"));
		System.out.println("Done checking decks");
	}
}
