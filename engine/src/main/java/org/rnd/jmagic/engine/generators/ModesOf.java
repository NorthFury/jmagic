package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

public class ModesOf extends SetGenerator
{
	public static ModesOf instance(SetGenerator what)
	{
		return new ModesOf(what);
	}

	private SetGenerator what;

	private ModesOf(SetGenerator what)
	{
		this.what = what;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();

		for(GameObject object: this.what.evaluate(state, thisObject).getAll(GameObject.class))
			ret.addAll(object.getModes());

		return ret;
	}

}
