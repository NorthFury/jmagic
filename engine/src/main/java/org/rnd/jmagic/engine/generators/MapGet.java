package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

import java.util.Map;

public class MapGet extends SetGenerator
{
	public static MapGet instance(SetGenerator key, Map<?, ?> map)
	{
		return new MapGet(key, map);
	}

	private SetGenerator key;
	private Map<?, ?> map;

	private MapGet(SetGenerator key, Map<?, ?> map)
	{
		this.key = key;
		this.map = map;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();
		for(Object object: this.key.evaluate(state, thisObject))
			if(this.map.containsKey(object))
				ret.add(this.map.get(object));
		return ret;
	}

}
