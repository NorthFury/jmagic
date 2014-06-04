package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;
import org.rnd.util.NumberRange;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class ExileChoice extends EventType
{	public static final EventType INSTANCE = new ExileChoice();

	 private ExileChoice()
	{
		super("EXILE_CHOICE");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.OBJECT;
	}

	@Override
	public boolean attempt(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		int required = getRange(parameters.get(Parameter.NUMBER)).getLower(0);

		Set<GameObject> chosen = new HashSet<GameObject>();
		for(GameObject object: parameters.get(Parameter.OBJECT).getAll(GameObject.class))
		{
			Map<Parameter, MagicSet> newParameters = new HashMap<Parameter, MagicSet>();
			newParameters.put(Parameter.CAUSE, parameters.get(Parameter.CAUSE));
			newParameters.put(Parameter.TO, new MagicSet(game.actualState.exileZone()));
			newParameters.put(Parameter.OBJECT, new MagicSet(object));
			if(createEvent(game, "Exile " + object + ".", MOVE_OBJECTS, newParameters).attempt(event))
			{
				chosen.add(object);
				if(chosen.size() >= required)
					return true;
			}
		}

		return false;
	}

	@Override
	public void makeChoices(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		Player player = parameters.get(Parameter.PLAYER).getOne(Player.class);
		NumberRange number = getRange(parameters.get(Parameter.NUMBER));

		// offer the choices to the player
		Set<GameObject> choices = parameters.get(Parameter.OBJECT).getAll(GameObject.class);
		Collection<GameObject> chosen = player.sanitizeAndChoose(game.actualState, number.getLower(0), number.getUpper(choices.size()), choices, PlayerInterface.ChoiceType.OBJECTS, PlayerInterface.ChooseReason.EXILE);
		if(number.contains(chosen.size()))
			event.allChoicesMade = true;

		event.putChoices(player, chosen);
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		MagicSet chosen = event.getChoices(parameters.get(Parameter.PLAYER).getOne(Player.class));

		Map<Parameter, MagicSet> moveParameters = new HashMap<Parameter, MagicSet>();
		moveParameters.put(Parameter.CAUSE, parameters.get(Parameter.CAUSE));
		moveParameters.put(Parameter.TO, new MagicSet(game.actualState.exileZone()));
		moveParameters.put(Parameter.OBJECT, chosen);
		if(parameters.containsKey(Parameter.HIDDEN))
			moveParameters.put(Parameter.HIDDEN, Empty.set);

		Event exileEvent = createEvent(game, "Exile " + chosen + ".", MOVE_OBJECTS, moveParameters);
		boolean status = exileEvent.perform(event, false);

		event.setResult(exileEvent.getResultGenerator());

		return event.allChoicesMade && status;
	}
}