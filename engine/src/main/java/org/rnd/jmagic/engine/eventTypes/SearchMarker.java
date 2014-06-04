package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;

import java.util.Map;

public final class SearchMarker extends EventType
{
	public static final EventType INSTANCE = new SearchMarker();

	private SearchMarker()
	{
		super("SEARCH_MARKER");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.PLAYER;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		event.setResult(parameters.get(Parameter.CARD));
		return true;
	}
};