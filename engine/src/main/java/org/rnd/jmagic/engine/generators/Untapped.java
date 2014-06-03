package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

/**
 * Evaluates to all untapped objects on the battlefield
 */
public class Untapped extends SetGenerator
{
	private static final Untapped _instance = new Untapped();

	public static Untapped instance()
	{
		return _instance;
	}

	private Untapped()
	{
		// Intentionally left ineffectual
	}

	static public MagicSet get(GameState state)
	{
		MagicSet ret = new MagicSet();
		for(GameObject o: state.battlefield())
			if(!o.isTapped())
				ret.add(o);
		return ret;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		return Untapped.get(state);
	}
}
