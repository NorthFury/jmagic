package org.rnd.jmagic.engine;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Tracks what {@link Player}/Planeswalkers a {@link GameObject} has
 * successfully attacked this turn. The value is a map from the
 * {@link GameObject}'s ID to a {@link java.util.Set} of IDs of {@link Player}s
 * or Planeswalkers successfully attacked this turn.
 */
public class SuccessfullyAttacked extends Tracker<Map<Integer, Set<Integer>>>
{
	private Map<Integer, Set<Integer>> values = new HashMap<Integer, Set<Integer>>();
	private Map<Integer, Set<Integer>> unmodifiable = Collections.unmodifiableMap(this.values);

	@Override
	public SuccessfullyAttacked clone()
	{
		SuccessfullyAttacked ret = (SuccessfullyAttacked)super.clone();
		ret.values = new HashMap<Integer, Set<Integer>>();
		for(Map.Entry<Integer, Set<Integer>> e: this.values.entrySet())
			ret.values.put(e.getKey(), new HashSet<Integer>(e.getValue()));
		ret.unmodifiable = Collections.unmodifiableMap(ret.values);
		return ret;
	}

	@Override
	protected Map<Integer, Set<Integer>> getValueInternal()
	{
		return this.unmodifiable;
	}

	@Override
	protected boolean match(GameState state, Event event)
	{
		return EventType.DECLARE_ONE_ATTACKER == event.type;
	}

	@Override
	protected void reset()
	{
		this.values.clear();
	}

	@Override
	protected void update(GameState state, Event event)
	{
		Set<Identified> defenders = event.parametersNow.get(EventType.Parameter.DEFENDER).evaluate(state, null).getAll(Identified.class);
		for(GameObject attacker: event.parametersNow.get(EventType.Parameter.OBJECT).evaluate(state, null).getAll(GameObject.class))
		{
			Set<Integer> attacked;
			if(this.values.containsKey(attacker.ID))
				attacked = this.values.get(attacker.ID);
			else
			{
				attacked = new HashSet<Integer>();
				this.values.put(attacker.ID, attacked);
			}

			for(Identified i: defenders)
				attacked.add(i.ID);
		}
	}
}
