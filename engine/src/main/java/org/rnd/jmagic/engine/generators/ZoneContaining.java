package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

/**
 * Evaluates to the zones that contain all the given objects.
 */
public class ZoneContaining extends SetGenerator
{
	private SetGenerator what;

	public static ZoneContaining instance(SetGenerator what)
	{
		return new ZoneContaining(what);
	}

	private ZoneContaining(SetGenerator what)
	{
		this.what = what;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();
		for(GameObject o: this.what.evaluate(state, thisObject).getAll(GameObject.class))
			if(-1 != o.zoneID)
				ret.add(state.get(o.zoneID));
		return ret;
	}
}
