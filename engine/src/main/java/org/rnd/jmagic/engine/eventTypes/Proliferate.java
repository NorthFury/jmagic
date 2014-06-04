package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Proliferate extends EventType
{	public static final EventType INSTANCE = new Proliferate();

	 private Proliferate()
	{
		super("PROLIFERATE");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.PLAYER;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		// 701.23. Proliferate
		//
		// 701.23a To proliferate means to choose any number of permanents
		// and/or players that have a counter, then give each exactly one
		// additional counter of a kind that permanent or player already
		// has.
		//
		// 701.23b If a permanent or player chosen this way has more than
		// one kind of counter, the player who is proliferating chooses
		// which kind of counter to add.

		MagicSet hasCounters = Intersect.instance(Union.instance(Permanents.instance(), Players.instance()), HasCounters.instance()).evaluate(game, null);

		boolean ret = true;

		Player player = parameters.get(Parameter.PLAYER).getOne(Player.class);

		List<Identified> chosen = player.sanitizeAndChoose(game.actualState, 0, null, hasCounters.getAll(Identified.class), PlayerInterface.ChoiceType.OBJECTS, PlayerInterface.ChooseReason.PROLIFERATE);

		if(!chosen.isEmpty())
		{
			for(Identified i: chosen)
			{
				Set<Counter.CounterType> counters = new HashSet<Counter.CounterType>();
				if(i instanceof GameObject)
					for(Counter c: ((GameObject)i).counters)
						counters.add(c.getType());
				else if(i instanceof Player)
					for(Counter c: ((Player)i).counters)
						counters.add(c.getType());

				Counter.CounterType newCounter = null;
				if(counters.size() == 1)
					newCounter = counters.iterator().next();
				else
				{
					PlayerInterface.ChooseParameters<Counter.CounterType> chooseParameters = new PlayerInterface.ChooseParameters<Counter.CounterType>(1, 1, new LinkedList<Counter.CounterType>(counters), PlayerInterface.ChoiceType.ENUM, PlayerInterface.ChooseReason.PROLIFERATE_CHOOSECOUNTER);
					chooseParameters.whatForID = i.ID;
					List<Counter.CounterType> chosenType = player.choose(chooseParameters);
					if(!chosenType.isEmpty())
						newCounter = chosenType.iterator().next();
				}

				if(newCounter == null)
					ret = false;
				else
				{
					HashMap<Parameter, MagicSet> putCounterParameters = new HashMap<Parameter, MagicSet>();
					putCounterParameters.put(Parameter.CAUSE, parameters.get(Parameter.CAUSE));
					putCounterParameters.put(Parameter.COUNTER, new MagicSet(newCounter));
					putCounterParameters.put(Parameter.OBJECT, new MagicSet(i));
					Event putCounter = createEvent(game, "Put a " + newCounter + " counter on " + i + ".", PUT_COUNTERS, putCounterParameters);
					if(!putCounter.perform(event, false))
						ret = false;
				}
			}
		}

		event.setResult(Empty.set);
		return ret;
	}
}