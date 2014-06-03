package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

public final class MillCards extends EventType
{	public static final EventType INSTANCE = new MillCards();

	 private MillCards()
	{
		super("MILL_CARDS");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.PLAYER;
	}

	@Override
	public boolean attempt(Game game, Event event, java.util.Map<Parameter, MagicSet> parameters)
	{
		int number = Sum.get(parameters.get(Parameter.NUMBER));

		for(Player player: parameters.get(Parameter.PLAYER).getAll(Player.class))
			if(player.getPhysical().getLibrary(game.physicalState).objects.size() < number)
				return false;

		return true;
	}

	@Override
	public boolean perform(Game game, Event event, java.util.Map<Parameter, MagicSet> parameters)
	{
		boolean allMilled = true;
		MagicSet cause = parameters.get(Parameter.CAUSE);
		int num = Sum.get(parameters.get(Parameter.NUMBER));

		if(num < 0)
			num = 0;

		MagicSet result = new MagicSet();

		for(Player player: parameters.get(Parameter.PLAYER).getAll(Player.class))
		{
			Zone graveyard = player.getGraveyard(game.actualState);
			Zone library = player.getLibrary(game.actualState);
			MagicSet topCards = TopCards.get(num, library);
			java.util.Map<Parameter, MagicSet> moveParameters = new java.util.HashMap<Parameter, MagicSet>();
			moveParameters.put(Parameter.CAUSE, cause);
			moveParameters.put(Parameter.TO, new MagicSet(graveyard));
			moveParameters.put(Parameter.OBJECT, topCards);

			Event move = createEvent(game, "Put " + topCards + " into " + graveyard + ".", MOVE_OBJECTS, moveParameters);
			if(!move.perform(event, false))
				allMilled = false;
			result.addAll(move.getResult());
		}

		event.setResult(Identity.instance(result));
		return allMilled;
	}
}