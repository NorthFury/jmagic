package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

public class Multicolored extends SetGenerator
{
	private static final Multicolored _instance = new Multicolored();

	public static Multicolored instance()
	{
		return _instance;
	}

	private Multicolored()
	{
		// Singleton Constructor
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();

		for(GameObject object: state.getAllObjects())
			if(object.getColors().size() > 1)
				ret.add(object);

		return ret;
	}
}
