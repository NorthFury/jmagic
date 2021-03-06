package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

/**
 * Evaluates to all players
 */
public class Players extends SetGenerator
{
	private static final Players _instance = new Players();

	public static Players instance()
	{
		return _instance;
	}

	public static MagicSet get(GameState state)
	{
		MagicSet ret = new MagicSet();
		for(Player p: state.players)
			if(!p.outOfGame)
				ret.add(p);
		return ret;
	}

	private Players()
	{
		// Intentionally non-functional
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		return Players.get(state);
	}
}
