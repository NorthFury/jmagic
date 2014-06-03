package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

/**
 * Evaluates to a factory that will generate an event that acts the same way as
 * the given event. If the new event is performed, its result will be passed to
 * the old event.
 */
public class EventParts extends SetGenerator
{
	public static SetGenerator instance(SetGenerator events)
	{
		return new EventParts(events);
	}

	private SetGenerator events;

	private EventParts(SetGenerator events)
	{
		this.events = events;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();
		for(Event event: this.events.evaluate(state, thisObject).getAll(Event.class))
		{
			EventFactory factory = new EventFactory(event.type, event.getName());
			factory.parameters.putAll(event.parameters);
			factory.passResultTo(event);
			ret.add(factory);
		}
		return ret;
	}

}
