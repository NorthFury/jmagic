package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.Map;

public final class SetLife extends EventType
{	public static final EventType INSTANCE = new SetLife();

	 private SetLife()
	{
		super("SET_LIFE");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.PLAYER;
	}

	@Override
	public boolean attempt(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		MagicSet cause = parameters.get(Parameter.CAUSE);
		int life = Sum.get(parameters.get(Parameter.NUMBER));
		for(Player player: parameters.get(Parameter.PLAYER).getAll(Player.class))
		{
			Map<Parameter, MagicSet> newParameters = new HashMap<Parameter, MagicSet>();
			newParameters.put(Parameter.PLAYER, new MagicSet(player));
			newParameters.put(Parameter.CAUSE, cause);

			// 118.5. If an effect sets a player's life total to a specific
			// number, the player gains or loses the necessary amount of
			// life to end up with the new total.
			Event lifeSet = null;
			if(life < player.lifeTotal)
			{
				int change = player.lifeTotal - life;
				newParameters.put(Parameter.NUMBER, new MagicSet(change));
				lifeSet = createEvent(game, player + " loses " + change + " life.", EventType.LOSE_LIFE, newParameters);
			}
			else if(player.lifeTotal < life)
			{
				int change = life - player.lifeTotal;
				newParameters.put(Parameter.NUMBER, new MagicSet(change));
				lifeSet = createEvent(game, player + " gains " + change + " life.", EventType.GAIN_LIFE, newParameters);
			}

			if(null != lifeSet)
				if(!lifeSet.attempt(event))
					return false;
		}

		return true;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		MagicSet cause = parameters.get(Parameter.CAUSE);
		MagicSet result = new MagicSet();
		int life = Sum.get(parameters.get(Parameter.NUMBER));
		boolean allSet = true;
		for(Player player: parameters.get(Parameter.PLAYER).getAll(Player.class))
		{
			Map<Parameter, MagicSet> newParameters = new HashMap<Parameter, MagicSet>();
			newParameters.put(Parameter.PLAYER, new MagicSet(player));
			newParameters.put(Parameter.CAUSE, cause);

			// 118.5. If an effect sets a player's life total to a specific
			// number, the player gains or loses the necessary amount of
			// life to end up with the new total.
			Event lifeSet = null;
			if(life < player.lifeTotal)
			{
				int change = player.lifeTotal - life;
				newParameters.put(Parameter.NUMBER, new MagicSet(change));
				lifeSet = createEvent(game, player + " loses " + change + " life.", EventType.LOSE_LIFE, newParameters);
			}
			else if(player.lifeTotal < life)
			{
				int change = life - player.lifeTotal;
				newParameters.put(Parameter.NUMBER, new MagicSet(change));
				lifeSet = createEvent(game, player + " gains " + change + " life.", EventType.GAIN_LIFE, newParameters);
			}

			if(null != lifeSet)
			{
				if(!lifeSet.perform(event, false))
					allSet = false;
				result.addAll(lifeSet.getResult());
			}
		}

		event.setResult(Identity.instance(result));
		return allSet;
	}
}