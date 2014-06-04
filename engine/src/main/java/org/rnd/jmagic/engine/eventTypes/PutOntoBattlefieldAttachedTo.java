package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.Map;

public final class PutOntoBattlefieldAttachedTo extends EventType
{	public static final EventType INSTANCE = new PutOntoBattlefieldAttachedTo();

	 private PutOntoBattlefieldAttachedTo()
	{
		super("PUT_ONTO_BATTLEFIELD_ATTACHED_TO");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.OBJECT;
	}

	@Override
	public boolean attempt(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		GameObject target = parameters.get(Parameter.TARGET).getOne(GameObject.class);

		if(target == null || target.isGhost())
			return false;

		Zone targetZone = target.getZone();

		if(targetZone == null || !targetZone.objects.contains(target) || targetZone.getPhysical() != game.physicalState.battlefield())
			return false;

		for(GameObject object: parameters.get(Parameter.OBJECT).getAll(GameObject.class))
			if(object.isGhost())
				return false;
		return true;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		for(GameObject attachment: parameters.get(Parameter.OBJECT).getAll(GameObject.class))
		{
			AttachableTo attachTo = parameters.get(Parameter.TARGET).getOne(AttachableTo.class);
			if(attachment.canAttachTo(game, attachTo))
			{
				Event putOntoBattlefield = createEvent(game, "Put " + parameters.get(Parameter.OBJECT) + " onto the battlefield.", PUT_ONTO_BATTLEFIELD, parameters);
				boolean status = putOntoBattlefield.perform(event, false);

				ZoneChange zoneChange = putOntoBattlefield.getResult().getOne(ZoneChange.class);

				EventFactory attach = new EventFactory(ATTACH, "Attach to target");
				attach.parameters.put(Parameter.OBJECT, NewObjectOf.instance(Identity.instance(zoneChange)));
				attach.parameters.put(Parameter.TARGET, Identity.instance(parameters.get(Parameter.TARGET)));

				zoneChange.events.add(attach);
				event.setResult(putOntoBattlefield.getResultGenerator());
				return status;
			}
		}

		event.setResult(Empty.set);
		return false;
	}
}