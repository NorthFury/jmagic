package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

public class Permanents extends SetGenerator
{
	private static final Permanents _instance = new Permanents();

	public static Permanents instance()
	{
		return _instance;
	}

	private Permanents()
	{
		// Singleton Generator
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();
		for(GameObject object: state.battlefield().objects)
			ret.add(state.get(object.ID));
		return ret;
	}
}
