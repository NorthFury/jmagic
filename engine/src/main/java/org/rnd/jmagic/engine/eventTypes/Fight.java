package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class Fight extends EventType
{	public static final EventType INSTANCE = new Fight();

	 private Fight()
	{
		super("FIGHT");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.OBJECT;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		Set<GameObject> fighters = new HashSet<GameObject>();

		fighters.addAll(parameters.get(Parameter.OBJECT).getAll(GameObject.class));

		for(Target target: parameters.get(Parameter.OBJECT).getAll(Target.class))
		{
			GameObject object = game.actualState.getByIDObject(target.targetID);
			if(object != null)
				fighters.add(object);
		}

		if(fighters.size() != 2)
		{
			event.setResult(Empty.set);
			return false;
		}

		Iterator<GameObject> iterator = fighters.iterator();

		GameObject one = iterator.next();
		MagicSet oneSet = new MagicSet(one);

		GameObject two = iterator.next();
		MagicSet twoSet = new MagicSet(two);

		Map<Parameter, MagicSet> oneParameters = new HashMap<Parameter, MagicSet>();
		oneParameters.put(Parameter.SOURCE, oneSet);
		oneParameters.put(Parameter.NUMBER, new MagicSet(one.getPower()));
		oneParameters.put(Parameter.TAKER, twoSet);
		Event oneDamage = createEvent(game, one + " deals damage equal to its power to " + two, EventType.DEAL_DAMAGE_EVENLY, oneParameters);

		Map<Parameter, MagicSet> twoParameters = new HashMap<Parameter, MagicSet>();
		twoParameters.put(Parameter.SOURCE, twoSet);
		twoParameters.put(Parameter.NUMBER, new MagicSet(two.getPower()));
		twoParameters.put(Parameter.TAKER, oneSet);
		Event twoDamage = createEvent(game, two + " deals damage equal to its power to " + one, EventType.DEAL_DAMAGE_EVENLY, twoParameters);

		boolean ret = true;
		if(!oneDamage.perform(event, false))
			ret = false;
		if(!twoDamage.perform(event, false))
			ret = false;

		if(ret)
			event.setResult(Identity.instance(one, two));
		return ret;
	}
}