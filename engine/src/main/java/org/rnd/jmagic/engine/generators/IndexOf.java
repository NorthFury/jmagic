package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

public class IndexOf extends SetGenerator
{
	public static IndexOf instance(SetGenerator what)
	{
		return new IndexOf(what);
	}

	private SetGenerator what;

	private IndexOf(SetGenerator what)
	{
		this.what = what;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();

		for(GameObject object: this.what.evaluate(state, thisObject).getAll(GameObject.class))
		{
			Zone zone = state.get(object.zoneID);
			if(null == zone)
				continue;
			int index = zone.objects.indexOf(object);

			// With zones, the top card is index 1, not index 0
			if(index >= 0)
				ret.add(index + 1);
		}

		return ret;
	}

}
