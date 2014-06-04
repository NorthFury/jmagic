package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public final class PayManaCost extends EventType
{	public static final EventType INSTANCE = new PayManaCost();

	 private PayManaCost()
	{
		super("PAY_MANA_COST");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.PLAYER;
	}

	@Override
	public boolean attempt(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		GameObject object = parameters.get(Parameter.OBJECT).getOne(GameObject.class);
		if(object == null || object.getManaCost() != null)
			return true;

		Player player = parameters.get(Parameter.PLAYER).getOne(Player.class);
		if(null != object.alternateCosts)
			for(AlternateCost c: object.alternateCosts)
				if(c.playersMayPay.contains(player))
					return true;

		return false;
	}

	@Override
	public void makeChoices(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		GameObject o = parameters.get(Parameter.OBJECT).getOne(GameObject.class);
		if(o == null)
			return;
		Player p = parameters.get(Parameter.PLAYER).getOne(Player.class);

		Collection<CostCollection> availableChoices = new LinkedList<CostCollection>();
		if(o.getManaCost() != null)
			availableChoices.add(new CostCollection(CostCollection.TYPE_MANA, o.getManaCost()));
		if(null != o.alternateCosts)
			for(AlternateCost c: o.alternateCosts)
				if(c.playersMayPay.contains(p))
					availableChoices.add(c.cost);

		CostCollection choice = p.sanitizeAndChoose(game.actualState, 1, availableChoices, PlayerInterface.ChoiceType.ALTERNATE_COST, PlayerInterface.ChooseReason.OPTIONAL_ALTERNATE_COST).get(0);
		event.putChoices(p, Collections.singleton(choice));
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		event.setResult(Empty.set);

		GameObject o = parameters.get(Parameter.OBJECT).getOne(GameObject.class);
		if(o == null)
			return true;
		Player p = parameters.get(Parameter.PLAYER).getOne(Player.class);
		CostCollection cost = event.getChoices(p).getOne(CostCollection.class);

		for(EventFactory f: cost.events)
			if(!f.createEvent(game, o).perform(event, true))
				return false;
		if(!cost.manaCost.isEmpty())
		{
			Map<Parameter, MagicSet> manaParameters = new HashMap<Parameter, MagicSet>();
			manaParameters.put(Parameter.CAUSE, new MagicSet(o));
			manaParameters.put(Parameter.OBJECT, new MagicSet(o));
			manaParameters.put(Parameter.PLAYER, new MagicSet(p));
			manaParameters.put(Parameter.COST, new MagicSet(cost.manaCost));
			Event payMana = createEvent(game, p + " pays " + cost.manaCost, EventType.PAY_MANA, manaParameters);
			if(!payMana.perform(event, true))
				return false;
		}

		return true;
	}
}