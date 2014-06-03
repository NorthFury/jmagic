package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

public class LandPermanents extends SetGenerator
{
	private static final LandPermanents _instance = new LandPermanents();

	public static LandPermanents instance()
	{
		return _instance;
	}

	private LandPermanents()
	{
		// Singleton Constructor
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();

		for(GameObject object: state.battlefield().objects)
			if(object.getTypes().contains(Type.LAND))
				ret.add(object);

		return ret;
	}
}
