package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.Map;

public final class PlayerMayPayMana extends EventType
{	public static final EventType INSTANCE = new PlayerMayPayMana();

	 private PlayerMayPayMana()
	{
		super("PLAYER_MAY_PAY_MANA");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.PLAYER;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		MagicSet playerParameter = parameters.get(Parameter.PLAYER);
		Player player = playerParameter.getOne(Player.class);
		player.mayActivateManaAbilities();

		MagicSet costParameter = parameters.get(Parameter.COST);
		ManaPool cost = new ManaPool(costParameter.getAll(ManaSymbol.class));
		int number = 1;
		if(parameters.containsKey(Parameter.NUMBER))
			number = Sum.get(parameters.get(Parameter.NUMBER));
		cost = cost.duplicate(number);

		EventFactory factory = new EventFactory(PAY_MANA, "Pay " + cost);
		factory.parameters.put(Parameter.CAUSE, Identity.instance(parameters.get(Parameter.CAUSE)));
		factory.parameters.put(Parameter.COST, Identity.instance(cost));
		factory.parameters.put(Parameter.PLAYER, Identity.instance(playerParameter));

		Map<Parameter, MagicSet> mayPayParameters = new HashMap<Parameter, MagicSet>();
		mayPayParameters.put(Parameter.PLAYER, playerParameter);
		mayPayParameters.put(Parameter.EVENT, new MagicSet(factory));
		Event mayPay = createEvent(game, player + " may pay " + cost + ".", PLAYER_MAY, mayPayParameters);
		boolean ret = mayPay.perform(event, false);

		event.setResult(mayPay.getResultGenerator());
		return ret;
	}
}