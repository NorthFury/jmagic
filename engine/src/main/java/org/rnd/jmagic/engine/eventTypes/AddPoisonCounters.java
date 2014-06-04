package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class AddPoisonCounters extends EventType
{	public static final EventType INSTANCE = new AddPoisonCounters();

	 private AddPoisonCounters()
	{
		super("ADD_POISON_COUNTERS");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.PLAYER;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		Set<Counter> counters = new HashSet<Counter>();
		int number = Sum.get(parameters.get(Parameter.NUMBER));
		Set<Player> players = parameters.get(Parameter.PLAYER).getAll(Player.class);
		for(Player player: players)
		{
			Player physical = player.getPhysical();
			for(int i = 0; i < number; ++i)
			{
				Counter counter = new Counter(Counter.CounterType.POISON, player.ID);
				if(physical.counters.add(counter))
					counters.add(counter);
			}
		}
		event.setResult(Identity.instance(counters));
		return true;
	}
}