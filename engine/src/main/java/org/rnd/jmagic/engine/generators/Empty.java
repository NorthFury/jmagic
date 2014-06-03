package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

/**
 * Evaluates to an empty set
 */
public class Empty extends SetGenerator
{
	public static final MagicSet set = new MagicSet.Unmodifiable();
	private static final SetGenerator _instance = new Empty();

	private Empty()
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

	@Override
	public java.util.Set<org.rnd.jmagic.engine.ManaSymbol.ManaType> extractColors(org.rnd.jmagic.engine.Game game, org.rnd.jmagic.engine.GameObject thisObject, java.util.Set<org.rnd.jmagic.engine.SetGenerator> ignoreThese) throws java.lang.NoSuchMethodException
	{
		return java.util.EnumSet.noneOf(ManaSymbol.ManaType.class);
	}
}
