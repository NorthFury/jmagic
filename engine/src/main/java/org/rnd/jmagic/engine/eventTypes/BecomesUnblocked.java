package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.Map;

public final class BecomesUnblocked extends EventType
{	public static final EventType INSTANCE = new BecomesUnblocked();

	 private BecomesUnblocked()
	{
		super("BECOMES_UNBLOCKED");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.ATTACKER;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		event.setResult(Empty.set);
		return true;
	}
}