package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.Map;

public final class DiscardTo extends EventType
{	public static final EventType INSTANCE = new DiscardTo();

	 private DiscardTo()
	{
		super("DISCARD_TO");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.PLAYER;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		// get the number of cards out of the parameter
		int numberOfCards = Sum.get(parameters.get(Parameter.NUMBER));

		if(numberOfCards < 0)
			numberOfCards = 0;

		MagicSet cause = parameters.get(Parameter.CAUSE);

		boolean allDiscarded = true;
		MagicSet result = new MagicSet();

		// get the player out of the actee and get the ID's of the cards in
		// their hand
		for(Player player: parameters.get(Parameter.PLAYER).getAll(Player.class))
		{
			int numberToDiscard = player.getHand(game.actualState).objects.size() - numberOfCards;
			if(numberToDiscard <= 0)
				continue;

			Map<Parameter, MagicSet> discardParameters = new HashMap<Parameter, MagicSet>();
			discardParameters.put(Parameter.CAUSE, cause);
			discardParameters.put(Parameter.PLAYER, new MagicSet(player));
			discardParameters.put(Parameter.NUMBER, new MagicSet(numberToDiscard));

			Event discard = createEvent(game, player + " discards " + numberToDiscard + " card" + (numberToDiscard == 1 ? "" : "s."), DISCARD_CHOICE, discardParameters);
			if(!discard.perform(event, false))
				result.addAll(discard.getResult());
			allDiscarded = false;
		}

		event.setResult(Identity.instance(result));
		return allDiscarded;
	}
}