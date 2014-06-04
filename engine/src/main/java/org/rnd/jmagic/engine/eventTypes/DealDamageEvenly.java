package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

public final class DealDamageEvenly extends EventType
{	public static final EventType INSTANCE = new DealDamageEvenly();

	 private DealDamageEvenly()
	{
		super("DEAL_DAMAGE_EVENLY");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.TAKER;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		GameObject source = parameters.get(Parameter.SOURCE).getOne(GameObject.class);
		int damageAmount = Sum.get(parameters.get(Parameter.NUMBER));
		boolean unpreventable = parameters.containsKey(Parameter.PREVENT);
		Collection<Identified> takers = new LinkedList<Identified>();
		takers.addAll(parameters.get(Parameter.TAKER).getAll(Player.class));
		takers.addAll(parameters.get(Parameter.TAKER).getAll(GameObject.class));

		for(Identified taker: takers)
			for(int i = 0; i < damageAmount; i++)
				event.addDamage(source, taker, unpreventable);

		event.setResult(Empty.set);
		return true;
	}
}