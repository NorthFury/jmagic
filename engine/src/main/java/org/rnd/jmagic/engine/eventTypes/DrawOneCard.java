package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

public final class DrawOneCard extends EventType
{	public static final EventType INSTANCE = new DrawOneCard();

	 private DrawOneCard()
	{
		super("DRAW_ONE_CARD");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.PLAYER;
	}

	@Override
	public boolean perform(Game game, Event event, java.util.Map<Parameter, MagicSet> parameters)
	{
		Player player = parameters.get(Parameter.PLAYER).getOne(Player.class);
		Zone library = player.getLibrary(game.actualState);

		if(library.objects.size() == 0)
		{
			player.getPhysical().unableToDraw = true;
			event.setResult(Empty.set);
			return false;
		}

		Zone hand = player.getHand(game.actualState);
		java.util.Map<Parameter, MagicSet> moveParameters = new java.util.HashMap<Parameter, MagicSet>();
		moveParameters.put(Parameter.CAUSE, parameters.get(Parameter.CAUSE));
		moveParameters.put(Parameter.TO, new MagicSet(hand));
		moveParameters.put(Parameter.OBJECT, new MagicSet(library.objects.get(0)));
		Event move = createEvent(game, "Put " + library.objects.get(0) + " into " + hand + ".", MOVE_OBJECTS, moveParameters);
		move.perform(event, false);

		ZoneChange change = move.getResult().getOne(ZoneChange.class);
		change.isDraw = true;

		event.setResult(move.getResultGenerator());
		return true;
	}
}