package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class ExchangeLifeTotals extends EventType
{	public static final EventType INSTANCE = new ExchangeLifeTotals();

	 private ExchangeLifeTotals()
	{
		super("EXCHANGE_LIFE_TOTALS");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.PLAYER;
	}

	@Override
	public boolean attempt(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		return this.attemptEvents(event, this.getEvents(game, parameters));
	}

	private boolean attemptEvents(Event parentEvent, Set<Event> events)
	{
		if(events == null)
			return false;
		for(Event event: events)
			if(!event.attempt(parentEvent))
				return false;
		return true;
	}

	private Set<Event> getEvents(Game game, Map<Parameter, MagicSet> parameters)
	{
		Set<Player> players = parameters.get(Parameter.PLAYER).getAll(Player.class);

		if(players.size() != 2)
			return null;

		Iterator<Player> iter = players.iterator();
		Player playerOne = iter.next();
		Player playerTwo = iter.next();
		int lifeOne = playerOne.lifeTotal;
		int lifeTwo = playerTwo.lifeTotal;

		Map<Parameter, MagicSet> parametersOne = new HashMap<Parameter, MagicSet>();
		parametersOne.put(Parameter.CAUSE, parameters.get(Parameter.CAUSE));
		parametersOne.put(Parameter.NUMBER, new MagicSet(lifeTwo));
		parametersOne.put(Parameter.PLAYER, new MagicSet(playerOne));
		Event eventOne = createEvent(game, playerOne + "'s life total becomes " + lifeTwo, EventType.SET_LIFE, parametersOne);

		Map<Parameter, MagicSet> parametersTwo = new HashMap<Parameter, MagicSet>();
		parametersTwo.put(Parameter.CAUSE, parameters.get(Parameter.CAUSE));
		parametersTwo.put(Parameter.NUMBER, new MagicSet(lifeOne));
		parametersTwo.put(Parameter.PLAYER, new MagicSet(playerTwo));
		Event eventTwo = createEvent(game, playerTwo + "'s life total becomes " + lifeOne, EventType.SET_LIFE, parametersTwo);

		Set<Event> ret = new HashSet<Event>();
		ret.add(eventOne);
		ret.add(eventTwo);
		return ret;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		event.setResult(Empty.set);

		Set<Event> events = this.getEvents(game, parameters);
		if(!this.attemptEvents(event, events))
			return false;

		boolean ret = true;

		for(Event childEvent: events)
			if(!childEvent.perform(event, false))
				ret = false;

		return ret;
	}
}