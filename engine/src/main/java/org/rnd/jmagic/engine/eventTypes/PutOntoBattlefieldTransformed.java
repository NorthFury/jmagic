package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.Map;

public final class PutOntoBattlefieldTransformed extends EventType
{	public static final EventType INSTANCE = new PutOntoBattlefieldTransformed();

	 private PutOntoBattlefieldTransformed()
	{
		super("PUT_ONTO_BATTLEFIELD_TRANSFORMED");
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
			if(object.isGhost())
				return false;
		return true;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		Event putOntoBattlefield = createEvent(game, "Put " + parameters.get(Parameter.OBJECT) + " onto the battlefield.", PUT_ONTO_BATTLEFIELD, parameters);
		boolean status = putOntoBattlefield.perform(event, false);

		for(ZoneChange change: putOntoBattlefield.getResult().getAll(ZoneChange.class))
		{
			EventFactory transform = new EventFactory(TRANSFORM_PERMANENT, event.getName());
			transform.parameters.put(EventType.Parameter.OBJECT, NewObjectOf.instance(Identity.instance(change)));
			change.events.add(transform);
		}

		event.setResult(putOntoBattlefield.getResultGenerator());
		return status;
	}
}