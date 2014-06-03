package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

/**
 * Evaluates to an arbitrary non-empty set
 */
public class NonEmpty extends SetGenerator
{
	public static final MagicSet set = new MagicSet.Unmodifiable(new Object());
	private static final SetGenerator _instance = new NonEmpty();

	private NonEmpty()
	{
		// singleton generator
	}

	public static SetGenerator instance()
	{
		return _instance;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		return set;
	}
}
