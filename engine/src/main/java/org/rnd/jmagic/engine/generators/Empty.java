package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

import java.lang.NoSuchMethodException;
import java.util.EnumSet;
import java.util.Set;

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
	public Set<ManaSymbol.ManaType> extractColors(Game game, GameObject thisObject, Set<SetGenerator> ignoreThese) throws NoSuchMethodException
	{
		return EnumSet.noneOf(ManaSymbol.ManaType.class);
	}
}
