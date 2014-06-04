package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.Map;

public final class RemovedLastCounter extends EventType
{	public static final EventType INSTANCE = new RemovedLastCounter();

	 private RemovedLastCounter()
	{
		super("REMOVED_LAST_COUNTER");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.OBJECT;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		event.setResult(Empty.set);
		return true;
	}
}