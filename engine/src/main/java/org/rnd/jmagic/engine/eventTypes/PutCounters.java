package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

public final class PutCounters extends EventType
{	public static final EventType INSTANCE = new PutCounters();

	 private PutCounters()
	{
		super("PUT_COUNTERS");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.OBJECT;
	}

	@Override
	public boolean perform(Game game, Event event, java.util.Map<Parameter, MagicSet> parameters)
	{
		java.util.Set<Counter.CounterType> counterTypes = parameters.get(Parameter.COUNTER).getAll(Counter.CounterType.class);
		java.util.Set<Identified> objects = parameters.get(Parameter.OBJECT).getAll(Identified.class);
		int number = 1;
		if(parameters.containsKey(Parameter.NUMBER))
			number = Sum.get(parameters.get(Parameter.NUMBER));

		boolean allPlaced = true;
		MagicSet result = new MagicSet();

		for(Counter.CounterType counterType: counterTypes)
			for(Identified object: objects)
			{
				for(int i = 0; i < number; i++)
				{
					java.util.Map<Parameter, MagicSet> counterParameters = new java.util.HashMap<Parameter, MagicSet>();
					counterParameters.put(Parameter.CAUSE, parameters.get(Parameter.CAUSE));
					counterParameters.put(Parameter.COUNTER, new MagicSet(counterType));
					counterParameters.put(Parameter.OBJECT, new MagicSet(object));
					Event putCounter = createEvent(game, "Put a " + counterType + " on " + object + ".", EventType.PUT_ONE_COUNTER, counterParameters);
					boolean status = putCounter.perform(event, false);
					if(!status)
						allPlaced = false;
					result.addAll(putCounter.getResult());
				}
			}

		event.setResult(Identity.instance(result));

		return allPlaced;
	}
}