package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

/**
 * Evaluates to all GameObjects in the given set that aren't ghosts
 */
public class Exists extends SetGenerator
{
	public static SetGenerator instance(SetGenerator what)
	{
		return new Exists(what);
	}

	private Exists(SetGenerator what)
	{
		this.what = what;
	}

	private SetGenerator what;

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();
		for(GameObject o: this.what.evaluate(state, thisObject).getAll(GameObject.class))
			if(!o.isGhost())
				ret.add(o);
		return ret;
	}

}
