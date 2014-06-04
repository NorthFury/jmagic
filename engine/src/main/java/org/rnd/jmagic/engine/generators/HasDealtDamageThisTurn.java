package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HasDealtDamageThisTurn extends SetGenerator
{
	private static HasDealtDamageThisTurn _instance = null;

	public static HasDealtDamageThisTurn instance()
	{
		if(_instance == null)
		{
			_instance = new HasDealtDamageThisTurn();
		}
		return _instance;
	}

	private HasDealtDamageThisTurn()
	{
		// Singleton constructor
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		Tracker<Map<Integer, Set<Integer>>> flag = state.getTracker(DealtDamageByThisTurn.DealtDamageByTracker.class);

		Set<Integer> ids = new HashSet<Integer>(flag.getValue(state).keySet());

		MagicSet ret = new MagicSet();
		for(Integer id: ids)
			ret.add(state.get(id));

		return ret;
	}

}
