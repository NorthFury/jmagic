package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.Map;

public final class DeclareOneBlocker extends EventType
{	public static final EventType INSTANCE = new DeclareOneBlocker();

	 private DeclareOneBlocker()
	{
		super("DECLARE_ONE_BLOCKER");
	}

	@Override
	public Parameter affects()
	{
		return null;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		event.setResult(Empty.set);
		return true;
	}
}