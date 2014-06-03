package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

public class ZoneCastFrom extends SetGenerator
{
	public static ZoneCastFrom instance(SetGenerator what)
	{
		return new ZoneCastFrom(what);
	}

	private final SetGenerator what;

	private ZoneCastFrom(SetGenerator what)
	{
		this.what = what;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();
		for(GameObject o: this.what.evaluate(state, thisObject).getAll(GameObject.class))
			if(-1 != o.zoneCastFrom)
				ret.add(state.<Zone>get(o.zoneCastFrom));
		return ret;
	}
}
