package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class RemoveOneCounter extends EventType
{	public static final EventType INSTANCE = new RemoveOneCounter();

	 private RemoveOneCounter()
	{
		super("REMOVE_ONE_COUNTER");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.OBJECT;
	}

	@Override
	public boolean attempt(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		Counter.CounterType counterType = parameters.get(Parameter.COUNTER).getOne(Counter.CounterType.class);
		GameObject object = parameters.get(Parameter.OBJECT).getOne(GameObject.class);
		if(object == null)
			return CountersOn.get(parameters.get(Parameter.OBJECT).getOne(Player.class), counterType).size() > 0;
		return CountersOn.get(object, counterType).size() > 0;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		Counter.CounterType counterType = parameters.get(Parameter.COUNTER).getOne(Counter.CounterType.class);

		Identified physicalObject = parameters.get(Parameter.OBJECT).getOne(Identified.class).getPhysical();

		Collection<Counter> counters;
		if(physicalObject.isGameObject())
			counters = ((GameObject)physicalObject).counters;
		else
			counters = ((Player)physicalObject).counters;
		MagicSet result = new MagicSet();

		boolean removed = false;
		boolean last = false;

		for(Counter counter: counters)
		{
			if(counter.getType().equals(counterType))
			{
				if(!removed)
				{
					last = true;
					removed = true;
					result.add(counter);
				}
				else if(last)
				{
					last = false;
					break;
				}
			}
		}

		counters.removeAll(result.getAll(Counter.class));

		event.setResult(Identity.instance(result));

		if(last)
		{
			Map<Parameter, MagicSet> lastCounterParameters = new HashMap<Parameter, MagicSet>();
			lastCounterParameters.put(Parameter.CAUSE, parameters.get(Parameter.CAUSE));
			lastCounterParameters.put(Parameter.COUNTER, new MagicSet(counterType));
			lastCounterParameters.put(Parameter.OBJECT, new MagicSet(physicalObject));
			Event removeLastCounter = createEvent(game, "Removed last " + counterType + " from " + physicalObject + ".", EventType.REMOVED_LAST_COUNTER, lastCounterParameters);
			removeLastCounter.perform(event, false);
		}

		return removed;
	}
}