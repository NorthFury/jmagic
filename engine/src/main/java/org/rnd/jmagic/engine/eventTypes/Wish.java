package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.Map;

public final class Wish extends EventType
{	public static final EventType INSTANCE = new Wish();

	 private Wish()
	{
		super("WISH");
	}

	@Override
	public Parameter affects()
	{
		return null;
	}

	@Override
	public boolean attempt(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		return !parameters.get(Parameter.CHOICE).getAll(GameObject.class).isEmpty();
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		event.setResult(Empty.set);

		MagicSet cause = parameters.get(Parameter.CAUSE);
		Player player = parameters.get(Parameter.PLAYER).getOne(Player.class);
		MagicSet choices = new MagicSet(player.sanitizeAndChoose(game.actualState, 0, 1, parameters.get(Parameter.CHOICE).getAll(GameObject.class), PlayerInterface.ChoiceType.OBJECTS, new PlayerInterface.ChooseReason(PlayerInterface.ChooseReason.GAME, event.getName(), false)));

		Map<EventType.Parameter, MagicSet> revealParameters = new HashMap<EventType.Parameter, MagicSet>();
		revealParameters.put(Parameter.CAUSE, cause);
		revealParameters.put(Parameter.OBJECT, choices);
		createEvent(game, "Reveal " + choices, EventType.REVEAL, revealParameters).perform(event, false);

		Map<EventType.Parameter, MagicSet> moveParameters = new HashMap<EventType.Parameter, MagicSet>();
		moveParameters.put(Parameter.CAUSE, cause);
		moveParameters.put(Parameter.TO, new MagicSet(player.getHand(game.actualState)));
		moveParameters.put(Parameter.OBJECT, choices);
		createEvent(game, "Put " + choices + " into your hand", EventType.MOVE_OBJECTS, moveParameters).perform(event, false);

		return true;
	}
}