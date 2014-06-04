package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public final class RemoveAllCounters extends EventType
{	public static final EventType INSTANCE = new RemoveAllCounters();

	 private RemoveAllCounters()
	{
		super("REMOVE_ALL_COUNTERS");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.OBJECT;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		MagicSet result = new MagicSet();
		MagicSet types = parameters.get(Parameter.COUNTER);

		for(GameObject object: parameters.get(Parameter.OBJECT).getAll(GameObject.class))
		{
			Collection<Event> removeEvents = new LinkedList<Event>();
			for(Counter counter: object.counters)
			{
				if(types == null || types.contains(counter.getType()))
				{
					Map<Parameter, MagicSet> counterParameters = new HashMap<Parameter, MagicSet>();
					counterParameters.put(Parameter.CAUSE, parameters.get(Parameter.CAUSE));
					counterParameters.put(Parameter.COUNTER, new MagicSet(counter.getType()));
					counterParameters.put(Parameter.OBJECT, new MagicSet(object));
					Event removeCounter = createEvent(game, "Remove a " + counter.getType() + " from " + object + ".", EventType.REMOVE_ONE_COUNTER, counterParameters);
					removeEvents.add(removeCounter);
				}
			}

			for(Event e: removeEvents)
			{
				e.perform(event, false);
				result.addAll(e.getResult());
			}
		}

		event.setResult(Identity.instance(result));
		return true;
	}
}