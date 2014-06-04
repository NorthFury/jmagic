package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.Map;

public final class GainLife extends EventType
{	public static final EventType INSTANCE = new GainLife();

	 private GainLife()
	{
		super("GAIN_LIFE");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.PLAYER;
	}

	@Override
	public boolean attempt(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		int lifeGain = Sum.get(parameters.get(Parameter.NUMBER));
		if(lifeGain <= 0)
			return true;

		MagicSet players = parameters.get(Parameter.PLAYER);
		for(Player player: players.getAll(Player.class))
		{
			HashMap<Parameter, MagicSet> newParameters = new HashMap<Parameter, MagicSet>(parameters);
			newParameters.put(Parameter.PLAYER, new MagicSet(player));
			Event gainLifeOnePlayer = createEvent(game, player + " loses " + lifeGain + " life", GAIN_LIFE_ONE_PLAYER, newParameters);
			if(!gainLifeOnePlayer.attempt(event))
				return false;
		}

		return true;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		int lifeGain = Sum.get(parameters.get(Parameter.NUMBER));
		if(lifeGain <= 0)
		{
			event.setResult(Empty.set);
			return true;
		}

		MagicSet result = new MagicSet();
		MagicSet players = parameters.get(Parameter.PLAYER);
		for(Player player: players.getAll(Player.class))
		{
			HashMap<Parameter, MagicSet> newParameters = new HashMap<Parameter, MagicSet>(parameters);
			newParameters.put(Parameter.PLAYER, new MagicSet(player));
			Event gain = createEvent(game, player + " gains " + lifeGain + " life", GAIN_LIFE_ONE_PLAYER, newParameters);
			gain.perform(event, false);
			result.addAll(gain.getResult());
		}

		event.setResult(result);
		return true;
	}
}