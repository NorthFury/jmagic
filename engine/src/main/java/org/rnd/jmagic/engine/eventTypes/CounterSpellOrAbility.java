package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.Map;

public final class CounterSpellOrAbility extends EventType
{	public static final EventType INSTANCE = new CounterSpellOrAbility();

	 private CounterSpellOrAbility()
	{
		super("COUNTER_SPELL_OR_ABILITY");
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
		boolean allCountered = true;
		MagicSet counterer = parameters.get(Parameter.CAUSE);
		Zone zone = (parameters.containsKey(Parameter.TO) ? parameters.get(Parameter.TO).getOne(Zone.class) : null);
		for(GameObject object: parameters.get(Parameter.OBJECT).getAll(GameObject.class))
		{
			Map<Parameter, MagicSet> counterParameters = new HashMap<Parameter, MagicSet>();
			counterParameters.put(Parameter.CAUSE, counterer);
			counterParameters.put(Parameter.OBJECT, new MagicSet(object));
			if(zone != null)
				counterParameters.put(Parameter.TO, new MagicSet(zone));
			Event counterOne = createEvent(game, "Counter " + object, EventType.COUNTER_ONE, counterParameters);
			counterOne.perform(event, false);
			result.addAll(counterOne.getResult());
		}

		event.setResult(Identity.instance(result));

		return allCountered;
	}
}