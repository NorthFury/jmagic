package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.Map;

public final class DiscardCards extends EventType
{	public static final EventType INSTANCE = new DiscardCards();

	 private DiscardCards()
	{
		super("DISCARD_CARDS");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.CARD;
	}

	@Override
	public boolean attempt(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		for(GameObject object: parameters.get(Parameter.CARD).getAll(Card.class))
		{
			if(object == null)
				return false;

			Zone zone = object.getZone();
			if(zone == null || object.isGhost() || !zone.isHand())
				return false;
		}
		return true;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		boolean allDiscarded = true;
		MagicSet result = new MagicSet();
		for(GameObject card: parameters.get(Parameter.CARD).getAll(Card.class))
		{
			Map<Parameter, MagicSet> discardParameters = new HashMap<Parameter, MagicSet>();
			discardParameters.put(Parameter.CAUSE, parameters.get(Parameter.CAUSE));
			discardParameters.put(Parameter.CARD, new MagicSet(card));
			Event discard = createEvent(game, card.getActual().getOwner(game.actualState) + " discards " + card + ".", DISCARD_ONE_CARD, discardParameters);
			if(!discard.perform(event, false))
				allDiscarded = false;
			result.addAll(discard.getResult());
		}

		event.setResult(Identity.instance(result));
		return allDiscarded;
	}
}