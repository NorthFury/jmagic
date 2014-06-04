package org.rnd.jmagic.engine.gameTypes.packWars;

import org.rnd.jmagic.CardLoader;
import org.rnd.jmagic.engine.*;

import java.util.List;

public interface BoosterFactory
{
	public List<Card> createBooster(GameState state) throws CardLoader.CardLoaderException;
}
