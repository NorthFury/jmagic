package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.Map;

public final class DrawCards extends EventType
{	public static final EventType INSTANCE = new DrawCards();

	 private DrawCards()
	{
		super("DRAW_CARDS");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.PLAYER;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		boolean allDrawn = true;
		MagicSet cause = parameters.get(Parameter.CAUSE);
		int numberOfCards = Sum.get(parameters.get(Parameter.NUMBER));
		MagicSet result = new MagicSet();

		// 120.2a If an effect instructs more than one player to draw cards,
		// the active player performs all of his or her draws first, then
		// each other player in turn order does the same.
		for(Player player: parameters.get(Parameter.PLAYER).getAll(Player.class))
		{
			Map<Parameter, MagicSet> drawParameters = new HashMap<Parameter, MagicSet>();
			drawParameters.put(Parameter.CAUSE, cause);

			// this player draws one card numberOfCards times
			for(int i = 0; i < numberOfCards; i++)
			{
				// get the 'new' actual player each time since his library
				// changed after the last draw
				drawParameters.put(Parameter.PLAYER, new MagicSet(game.actualState.get(player.ID)));

				Event drawOne = createEvent(game, player + " draws a card.", DRAW_ONE_CARD, drawParameters);
				if(!drawOne.perform(event, true))
					allDrawn = false;
				result.addAll(drawOne.getResult());
			}
		}

		event.setResult(Identity.instance(result));
		return allDrawn;
	}
}