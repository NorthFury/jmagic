package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.Map;

public final class TapPermanents extends EventType
{	public static final EventType INSTANCE = new TapPermanents();

	 private TapPermanents()
	{
		super("TAP_PERMANENTS");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.OBJECT;
	}

	@Override
	public boolean attempt(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		for(GameObject object: parameters.get(Parameter.OBJECT).getAll(GameObject.class))
			if(object.isTapped())
				return false;

		return true;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		MagicSet result = new MagicSet();

		boolean allTapped = true;
		for(GameObject actualObject: parameters.get(Parameter.OBJECT).getAll(GameObject.class))
		{
			Map<Parameter, MagicSet> tapOneParameters = new HashMap<Parameter, MagicSet>();
			tapOneParameters.put(Parameter.CAUSE, parameters.get(Parameter.CAUSE));
			tapOneParameters.put(Parameter.OBJECT, new MagicSet(actualObject));
			Event tapOne = createEvent(game, "Tap " + actualObject + ".", TAP_ONE_PERMANENT, tapOneParameters);

			if(!tapOne.perform(event, false))
				allTapped = false;
			result.addAll(tapOne.getResult());
		}
		event.setResult(Identity.instance(result));
		return allTapped;
	}
}