package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

/**
 * Evaluates to each tapped GameObject on the battlefield
 */
public class Tapped extends SetGenerator
{
	private static final Tapped _instance = new Tapped();

	public static Tapped instance()
	{
		return _instance;
	}

	public static MagicSet get(GameState state)
	{
		MagicSet ret = new MagicSet();
		for(GameObject o: state.battlefield())
			if(o.isTapped())
				ret.add(o);
		return ret;
	}

	private Tapped()
	{
		// Intentionally left ineffectual
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		return Tapped.get(state);
	}
}
