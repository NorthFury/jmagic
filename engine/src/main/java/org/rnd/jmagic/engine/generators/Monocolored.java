package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

public class Monocolored extends SetGenerator
{
	private static final Monocolored _instance = new Monocolored();

	public static Monocolored instance()
	{
		return _instance;
	}

	private Monocolored()
	{
		// Singleton Constructor
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();

		for(GameObject object: state.getAllObjects())
			if(object.getColors().size() == 1)
				ret.add(object);

		return ret;
	}
}
