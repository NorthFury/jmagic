package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

import java.util.Map;

public class MapKeySet extends SetGenerator
{
	public static MapKeySet instance(Map<?, ?> map)
	{
		return new MapKeySet(map);
	}

	private Map<?, ?> map;

	private MapKeySet(Map<?, ?> map)
	{
		this.map = map;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		return new MagicSet(this.map.keySet());
	}
}
