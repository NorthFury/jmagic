package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

/**
 * Evaluates to each GameObject whose mana cost contains (X).
 */
public class ManaCostContainsX extends SetGenerator
{
	public static ManaCostContainsX instance()
	{
		return _instance;
	}

	private static final ManaCostContainsX _instance = new ManaCostContainsX();

	private ManaCostContainsX()
	{
		// singleton generator
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();

		for(GameObject object: state.getAllObjects())
		{
			ManaPool manaCost = object.getManaCost();
			if(manaCost == null)
				continue;

			for(ManaSymbol m: manaCost)
				if(m.isX)
				{
					ret.add(object);
					break;
				}
		}

		return ret;
	}
}
