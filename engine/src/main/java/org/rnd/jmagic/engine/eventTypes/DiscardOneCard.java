package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;

import java.util.HashMap;
import java.util.Map;

public final class DiscardOneCard extends EventType
{	public static final EventType INSTANCE = new DiscardOneCard();

	 private DiscardOneCard()
	{
		super("DISCARD_ONE_CARD");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.CARD;
	}

	@Override
	public boolean attempt(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		GameObject card = parameters.get(Parameter.CARD).getOne(Card.class);

		if(card == null)
			return false;

		Zone zone = card.getZone();

		return (zone != null && !card.isGhost() && zone.isHand());
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		MagicSet card = parameters.get(Parameter.CARD);
		Player player = card.getOne(Card.class).getOwner(game.actualState);

		Zone to;
		Player controller = null;
		if(parameters.containsKey(Parameter.TO))
		{
			to = parameters.get(Parameter.TO).getOne(Zone.class);
			if(parameters.containsKey(Parameter.CONTROLLER))
				controller = parameters.get(Parameter.CONTROLLER).getOne(Player.class);
		}
		else
			to = player.getGraveyard(game.actualState);

		Map<Parameter, MagicSet> moveParameters = new HashMap<Parameter, MagicSet>();
		moveParameters.put(Parameter.CAUSE, parameters.get(Parameter.CAUSE));
		moveParameters.put(Parameter.TO, new MagicSet(to));
		moveParameters.put(Parameter.OBJECT, card);
		if(controller != null)
			moveParameters.put(Parameter.CONTROLLER, new MagicSet(controller));

		Event move = createEvent(game, "Move " + card + " to " + to + ".", MOVE_OBJECTS, moveParameters);
		move.perform(event, false);

		ZoneChange change = move.getResult().getOne(ZoneChange.class);
		change.isDiscard = true;

		event.setResult(move.getResultGenerator());
		return true;
	}
}