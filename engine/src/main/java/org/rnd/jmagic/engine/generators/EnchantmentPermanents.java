package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

public class EnchantmentPermanents extends SetGenerator
{
	private static final SetGenerator _instance = new EnchantmentPermanents();

	public static SetGenerator instance()
	{
		return _instance;
	}

	private EnchantmentPermanents()
	{
		// Singleton Constructor
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();

		for(GameObject object: state.battlefield().objects)
			if(object.getTypes().contains(Type.ENCHANTMENT))
				ret.add(object);

		return ret;
	}
}
