package org.rnd.jmagic.engine.gameTypes;

import org.rnd.jmagic.CardLoader;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.gameTypes.packWars.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Name("Pack wars")
@Description("Initial decks are replaced with the shuffled contents of boosters created by booster factories")
public class PackWars extends GameType.SimpleGameTypeRule
{
	private List<BoosterFactory> boosterFactories;

	public PackWars()
	{
		this.boosterFactories = new LinkedList<BoosterFactory>();
	}

	public PackWars(BoosterFactory... boosterFactories)
	{
		this();
		this.setBoosterFactories(boosterFactories);
	}

	public BoosterFactory[] getBoosterFactories()
	{
		return this.boosterFactories.toArray(new BoosterFactory[this.boosterFactories.size()]);
	}

	public BoosterFactory getBoosterFactories(int index)
	{
		return this.boosterFactories.get(index);
	}

	@Override
	public void modifyGameState(GameState physicalState)
	{
		try
		{
			for(Player player: physicalState.players)
			{
				Zone library = player.getLibrary(physicalState);
				for(GameObject o: library)
					physicalState.game.removeObject(o.getActual());
				library.objects.clear();

				Zone sideboard = player.getSideboard(physicalState);
				for(GameObject o: sideboard)
					physicalState.game.removeObject(o.getActual());
				sideboard.objects.clear();

				List<Card> deck = new LinkedList<Card>();

				for(BoosterFactory factory: this.boosterFactories)
					deck.addAll(factory.createBooster(physicalState));

				Collections.shuffle(deck);

				for(Card card: deck)
				{
					card.setOwner(player);
					library.addToTop(card);
				}
			}
		}
		catch(CardLoader.CardLoaderException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	public void setBoosterFactories(BoosterFactory[] factories)
	{
		this.boosterFactories = Arrays.asList(factories);
	}

	public void setBoosterFactories(int index, BoosterFactory factory)
	{
		while(index >= this.boosterFactories.size())
			this.boosterFactories.add(null);
		this.boosterFactories.set(index, factory);
	}
}
