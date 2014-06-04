package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.Map;

public final class DestroyPermanents extends EventType
{	public static final EventType INSTANCE = new DestroyPermanents();

	 private DestroyPermanents()
	{
		super("DESTROY_PERMANENTS");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.PERMANENT;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		boolean allDestroyed = true;
		MagicSet cause = parameters.get(Parameter.CAUSE);
		MagicSet result = new MagicSet();
		for(GameObject object: parameters.get(Parameter.PERMANENT).getAll(GameObject.class))
		{
			if(object.isPermanent())
			{
				Map<Parameter, MagicSet> destroyParameters = new HashMap<Parameter, MagicSet>();
				destroyParameters.put(Parameter.CAUSE, cause);
				destroyParameters.put(Parameter.PERMANENT, new MagicSet(object));
				Event destroy = createEvent(game, "Destroy " + object + ".", EventType.DESTROY_ONE_PERMANENT, destroyParameters);
				if(!destroy.perform(event, false))
					allDestroyed = false;
				result.addAll(destroy.getResult());
			}
			else
				allDestroyed = false;
		}

		event.setResult(Identity.instance(result));
		return allDestroyed;
	}
}