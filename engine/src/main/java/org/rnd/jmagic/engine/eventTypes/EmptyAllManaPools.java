package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.Map;

public final class EmptyAllManaPools extends EventType
{	public static final EventType INSTANCE = new EmptyAllManaPools();

	 private EmptyAllManaPools()
	{
		super("EMPTY_ALL_MANA_POOLS");
	}

	@Override
	public Parameter affects()
	{
		return null;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		Map<Parameter, MagicSet> emptyPoolParameters = new HashMap<Parameter, MagicSet>();
		emptyPoolParameters.put(Parameter.CAUSE, new MagicSet(game));
		emptyPoolParameters.put(Parameter.PLAYER, new MagicSet(game.actualState.players));
		Event emptyPools = createEvent(game, event.getName(), EMPTY_MANA_POOL, emptyPoolParameters);
		emptyPools.perform(event, false);

		event.setResult(Empty.set);
		return true;
	}
}