package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;
import org.rnd.util.NumberRange;

import java.util.HashMap;
import java.util.Map;

public final class Scry extends EventType
{	public static final EventType INSTANCE = new Scry();

	 private Scry()
	{
		super("SCRY");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.PLAYER;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		int num = Sum.get(parameters.get(Parameter.NUMBER));
		if(num < 0)
			num = 0;

		Player player = parameters.get(Parameter.PLAYER).getOne(Player.class);
		Zone library = player.getLibrary(game.actualState);

		MagicSet topCards = TopCards.instance(num, Identity.instance(library)).evaluate(game, null);

		Map<Parameter, MagicSet> lookParameters = new HashMap<Parameter, MagicSet>();
		lookParameters.put(Parameter.CAUSE, parameters.get(Parameter.CAUSE));
		lookParameters.put(Parameter.OBJECT, topCards);
		lookParameters.put(Parameter.PLAYER, new MagicSet(player));
		Event lookEvent = createEvent(game, "Look at the top " + num + " cards of your library.", EventType.LOOK, lookParameters);
		lookEvent.perform(event, true);

		player = player.getActual();
		library = library.getActual();

		Map<Parameter, MagicSet> moveParameters = new HashMap<Parameter, MagicSet>();
		moveParameters.put(Parameter.CAUSE, parameters.get(Parameter.CAUSE));
		moveParameters.put(Parameter.NUMBER, new MagicSet(new NumberRange(0, num)));
		moveParameters.put(Parameter.FROM, new MagicSet(library));
		moveParameters.put(Parameter.TO, new MagicSet(library));
		moveParameters.put(Parameter.OBJECT, topCards);
		moveParameters.put(Parameter.CHOICE, new MagicSet(PlayerInterface.ChooseReason.SCRY_TO_BOTTOM));
		moveParameters.put(Parameter.INDEX, new MagicSet(-1));
		moveParameters.put(Parameter.PLAYER, new MagicSet(player));
		Event moveEvent = createEvent(game, "Put any number of them on the bottom of your library.", EventType.MOVE_CHOICE, moveParameters);
		moveEvent.perform(event, true);

		player = player.getActual();
		library = library.getActual();

		Map<Parameter, MagicSet> reorderParameters = new HashMap<Parameter, MagicSet>();
		reorderParameters.put(Parameter.CAUSE, parameters.get(Parameter.CAUSE));
		reorderParameters.put(Parameter.OBJECT, topCards);
		reorderParameters.put(Parameter.TO, new MagicSet(library));
		reorderParameters.put(Parameter.INDEX, new MagicSet(1));
		reorderParameters.put(Parameter.PLAYER, new MagicSet(player));
		Event reorderEvent = createEvent(game, "Put the rest on top of your library in any order.", EventType.MOVE_OBJECTS, reorderParameters);
		reorderEvent.perform(event, true);

		event.setResult(Empty.set);

		return true;
	}
}