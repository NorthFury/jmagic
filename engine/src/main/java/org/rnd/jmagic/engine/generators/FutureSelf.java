package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

public class FutureSelf extends SetGenerator
{
	public static FutureSelf instance(SetGenerator what)
	{
		return new FutureSelf(what);
	}

	private SetGenerator what;

	private FutureSelf(SetGenerator what)
	{
		this.what = what;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();

		for(GameObject o: this.what.evaluate(state, thisObject).getAll(GameObject.class))
			if(o.futureSelf != -1)
				ret.add(state.get(o.futureSelf));

		return ret;
	}

}
