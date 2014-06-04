package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;
import org.rnd.util.NumberRange;

import java.util.HashMap;
import java.util.Map;

public final class PlayerMayPayX extends EventType
{	public static final EventType INSTANCE = new PlayerMayPayX();

	 private PlayerMayPayX()
	{
		super("PLAYER_MAY_PAY_X");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.PLAYER;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		MagicSet causeParameter = parameters.get(Parameter.CAUSE);
		GameObject cause = causeParameter.getOne(GameObject.class);

		MagicSet playerParameter = parameters.get(Parameter.PLAYER);
		Player player = playerParameter.getOne(Player.class);
		player.mayActivateManaAbilities();
		player = player.getActual();

		MagicSet manaParameter = parameters.get(Parameter.MANA);
		ManaPool additional = manaParameter == null ? new ManaPool() : new ManaPool(manaParameter.getAll(ManaSymbol.class));
		if(!player.pool.pays(game.actualState, additional))
		{
			event.setResult(Empty.set);
			return false;
		}

		int max = player.pool.size() - additional.size();
		boolean valid = false;
		while(!valid)
		{
			NumberRange range = new NumberRange(0, max);
			int X = player.chooseNumber(range, "Choose a value for X.");
			if(X == 0)
			{
				event.setResult(Empty.set);
				return true;
			}

			// Performing the PAY_MANA event will expand X, so we need to
			// make a separate ManaPool to attempt the payment with.
			ManaPool attemptCost = new ManaPool("X");
			if(parameters.containsKey(Parameter.MANA))
				attemptCost.addAll(additional);
			attemptCost.expandX(X, ManaSymbol.ManaType.COLORLESS);
			if(player.pool.pays(game.actualState, attemptCost))
			{
				// Performing the PAY_MANA event expands X using the value
				// of X of the cause.
				cause.getActual().setValueOfX(X);
				cause.getPhysical().setValueOfX(X);
				valid = true;
				event.setResult(new MagicSet(X));
			}
		}

		ManaPool cost = new ManaPool("X");
		cost.addAll(additional);

		Map<Parameter, MagicSet> payParameters = new HashMap<Parameter, MagicSet>();
		payParameters.put(EventType.Parameter.CAUSE, causeParameter);
		payParameters.put(EventType.Parameter.PLAYER, playerParameter);
		payParameters.put(EventType.Parameter.COST, new MagicSet(cost));
		Event pay = createEvent(game, "Pay (X)", PAY_MANA, payParameters);
		pay.perform(event, false);

		return true;
	}
}