package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.Map;

public final class LoseLife extends EventType
{	public static final EventType INSTANCE = new LoseLife();

	 private LoseLife()
	{
		super("LOSE_LIFE");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.PLAYER;
	}

	@Override
	public boolean attempt(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		int lifeloss = Sum.get(parameters.get(Parameter.NUMBER));
		if(lifeloss <= 0)
			return true;

		MagicSet players = parameters.get(Parameter.PLAYER);
		for(Player player: players.getAll(Player.class))
		{
			HashMap<Parameter, MagicSet> newParameters = new HashMap<Parameter, MagicSet>(parameters);
			newParameters.put(Parameter.PLAYER, new MagicSet(player));
			Event loseLifeOnePlayer = createEvent(game, player + " loses " + lifeloss + " life", LOSE_LIFE_ONE_PLAYER, newParameters);
			if(!loseLifeOnePlayer.attempt(event))
				return false;
		}

		return true;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		int lifeloss = Sum.get(parameters.get(Parameter.NUMBER));
		if(lifeloss <= 0)
		{
			event.setResult(Empty.set);
			return true;
		}

		MagicSet players = parameters.get(Parameter.PLAYER);
		MagicSet result = new MagicSet();
		for(Player player: players.getAll(Player.class))
		{
			HashMap<Parameter, MagicSet> newParameters = new HashMap<Parameter, MagicSet>(parameters);
			newParameters.put(Parameter.PLAYER, new MagicSet(player));
			Event loseLifeOnePlayer = createEvent(game, player + " loses " + lifeloss + " life", LOSE_LIFE_ONE_PLAYER, newParameters);
			loseLifeOnePlayer.perform(event, false);
			result.addAll(loseLifeOnePlayer.getResult());
		}

		event.setResult(result);
		return true;
	}
}