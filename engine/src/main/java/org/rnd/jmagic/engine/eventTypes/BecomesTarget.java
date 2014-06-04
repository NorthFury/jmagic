package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.Map;

public final class BecomesTarget extends EventType
{	public static final EventType INSTANCE = new BecomesTarget();

	 private BecomesTarget()
	{
		super("BECOMES_TARGET");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.TARGET;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		event.setResult(Empty.set);
		return true;
	}
}