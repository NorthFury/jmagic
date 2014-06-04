package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WasDealtDamageThisTurn extends SetGenerator
{
	private static WasDealtDamageThisTurn _instance = null;

	public static WasDealtDamageThisTurn instance()
	{
		if(_instance == null)
		{
			_instance = new WasDealtDamageThisTurn();
		}
		return _instance;
	}

	private WasDealtDamageThisTurn()
	{
		// Singleton constructor
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		Tracker<Map<Integer, Set<Integer>>> flag = state.getTracker(DealtDamageByThisTurn.DealtDamageByTracker.class);

		Set<Integer> ids = new HashSet<Integer>();
		for(Set<Integer> damagers: flag.getValue(state).values())
			ids.addAll(damagers);

		MagicSet ret = new MagicSet();
		for(Integer id: ids)
			ret.add(state.get(id));

		return ret;
	}

}
