package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

/**
 * Evaluates to the battlefield
 */
public class Battlefield extends SetGenerator
{
	private static final Battlefield _instance = new Battlefield();

	public static Battlefield instance()
	{
		return _instance;
	}

	private Battlefield()
	{
		// Intentionally non-functional
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		return new MagicSet(state.battlefield());
	}
}
