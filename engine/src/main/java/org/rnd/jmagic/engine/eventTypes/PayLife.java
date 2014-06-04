package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.Map;

public final class PayLife extends EventType
{	public static final EventType INSTANCE = new PayLife();

	 private PayLife()
	{
		super("PAY_LIFE");
	}

	@Override
	public Parameter affects()
	{
		return LOSE_LIFE.affects();
	}

	@Override
	public boolean attempt(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		int number = Sum.get(parameters.get(Parameter.NUMBER));

		// 117.3b. ... (Players can always pay 0 life.)
		if(number == 0)
			return true;

		// 118.4. If a cost or effect allows a player to pay an amount of
		// life greater than 0, the player may do so only if his or her life
		// total is greater than or equal to the amount of the payment. ...
		Player player = parameters.get(Parameter.PLAYER).getOne(Player.class);
		if(player.lifeTotal < number)
			return false;

		// 118.4. If a player pays life, the payment is subtracted from his
		// or her life total; in other words, the player loses that much
		// life.
		// They can't pay life if they can't lose it.
		HashMap<Parameter, MagicSet> newParameters = new HashMap<Parameter, MagicSet>(parameters);
		newParameters.put(Parameter.PLAYER, new MagicSet(player));
		Event loseLifeOnePlayer = createEvent(game, player + " loses " + number + " life", LOSE_LIFE_ONE_PLAYER, newParameters);
		return loseLifeOnePlayer.attempt(event);
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		boolean status = this.attempt(game, event, parameters);

		Event loseLife = createEvent(game, event.getName(), LOSE_LIFE, parameters);
		status = loseLife.perform(event, false) && status;

		event.setResult(loseLife.getResultGenerator());
		return status;
	}
}