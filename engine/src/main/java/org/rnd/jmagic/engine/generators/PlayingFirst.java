package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

/**
 * Evaluates to the player with priority
 */
public class PlayingFirst extends org.rnd.jmagic.engine.SetGenerator
{
	private static final PlayingFirst _instance = new PlayingFirst();

	public static PlayingFirst instance()
	{
		return _instance;
	}

	private PlayingFirst()
	{
		// singleton
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		if(state.playingFirstID == -1)
			return Empty.set;
		return new MagicSet(state.<Player>get(state.playingFirstID));
	}
}
