package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

public class NameOf extends SetGenerator
{
	public static NameOf instance(SetGenerator what)
	{
		return new NameOf(what);
	}

	private SetGenerator what;

	private NameOf(SetGenerator what)
	{
		this.what = what;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();
		for(Identified i: this.what.evaluate(state, thisObject).getAll(Identified.class))
			ret.add(i.getName());
		return ret;
	}

}
