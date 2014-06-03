package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

public final class UntapPermanents extends EventType
{	public static final EventType INSTANCE = new UntapPermanents();

	 private UntapPermanents()
	{
		super("UNTAP_PERMANENTS");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.OBJECT;
	}

	@Override
	public boolean attempt(Game game, Event event, java.util.Map<Parameter, MagicSet> parameters)
	{
		for(GameObject object: parameters.get(Parameter.OBJECT).getAll(GameObject.class))
			if(!object.isTapped())
				return false;

		return true;
	}

	@Override
	public boolean perform(Game game, Event event, java.util.Map<Parameter, MagicSet> parameters)
	{
		MagicSet result = new MagicSet();

		boolean allUntapped = true;
		for(GameObject actualObject: parameters.get(Parameter.OBJECT).getAll(GameObject.class))
		{
			java.util.Map<Parameter, MagicSet> tapOneParameters = new java.util.HashMap<Parameter, MagicSet>();
			tapOneParameters.put(Parameter.CAUSE, parameters.get(Parameter.CAUSE));
			tapOneParameters.put(Parameter.OBJECT, new MagicSet(actualObject));
			Event untapOne = createEvent(game, "Untap " + actualObject + ".", UNTAP_ONE_PERMANENT, tapOneParameters);

			if(!untapOne.perform(event, false))
				allUntapped = false;
			result.addAll(untapOne.getResult());
		}
		event.setResult(Identity.instance(result));
		return allUntapped;
	}
}