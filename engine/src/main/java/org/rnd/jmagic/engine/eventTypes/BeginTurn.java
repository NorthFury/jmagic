package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.Map;

public final class BeginTurn extends EventType
{	public static final EventType INSTANCE = new BeginTurn();

	 private BeginTurn()
	{
		super("BEGIN_TURN");
	}

	@Override
	public Parameter affects()
	{
		return null;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		Turn turn = parameters.get(Parameter.TURN).getOne(Turn.class);
		game.physicalState.setCurrentTurn(turn);
		event.setResult(Identity.instance(turn));
		return true;
	}
}