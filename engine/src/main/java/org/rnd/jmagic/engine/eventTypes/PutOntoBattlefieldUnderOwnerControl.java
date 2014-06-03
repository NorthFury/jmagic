package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

public final class PutOntoBattlefieldUnderOwnerControl extends EventType
{	public static final EventType INSTANCE = new PutOntoBattlefieldUnderOwnerControl();

	 private PutOntoBattlefieldUnderOwnerControl()
	{
		super("PUT_ONTO_BATTLEFIELD_UNDER_OWNER_CONTROL");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.OBJECT;
	}

	@Override
	public boolean perform(Game game, Event event, java.util.Map<Parameter, MagicSet> parameters)
	{
		java.util.Map<Player, MagicSet> ownerMap = new java.util.HashMap<Player, MagicSet>();
		for(GameObject object: parameters.get(Parameter.OBJECT).getAll(GameObject.class))
		{
			Player owner = object.getOwner(game.actualState);
			if(ownerMap.containsKey(owner))
				ownerMap.get(owner).add(object);
			else
				ownerMap.put(owner, new MagicSet(object));
		}

		boolean allMoved = true;
		MagicSet cause = parameters.get(Parameter.CAUSE);
		MagicSet result = new MagicSet();
		for(java.util.Map.Entry<Player, MagicSet> entry: ownerMap.entrySet())
		{
			Player owner = entry.getKey();
			MagicSet move = entry.getValue();

			java.util.Map<Parameter, MagicSet> moveParameters = new java.util.HashMap<Parameter, MagicSet>();
			moveParameters.put(Parameter.CAUSE, cause);
			moveParameters.put(Parameter.CONTROLLER, new MagicSet(owner));
			moveParameters.put(Parameter.OBJECT, move);
			Event putOntoBattlefield = createEvent(game, "Put " + move + " onto the battlefield.", PUT_ONTO_BATTLEFIELD, moveParameters);

			if(!putOntoBattlefield.perform(event, false))
				allMoved = false;
			result.addAll(putOntoBattlefield.getResult());
		}

		event.setResult(Identity.instance(result));
		return allMoved;
	}
}