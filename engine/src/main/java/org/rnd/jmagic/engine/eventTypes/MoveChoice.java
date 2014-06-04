package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;
import org.rnd.util.NumberRange;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class MoveChoice extends EventType
{	public static final EventType INSTANCE = new MoveChoice();

	 private MoveChoice()
	{
		super("MOVE_CHOICE");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.OBJECT;
	}

	@Override
	public boolean attempt(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		boolean hidden = parameters.containsKey(Parameter.HIDDEN);
		int required = getRange(parameters.get(Parameter.NUMBER)).getLower(0);

		Set<GameObject> chosen = new HashSet<GameObject>();
		for(GameObject object: parameters.get(Parameter.OBJECT).getAll(GameObject.class))
		{
			Map<Parameter, MagicSet> newParameters = new HashMap<Parameter, MagicSet>(parameters);
			newParameters.put(Parameter.OBJECT, new MagicSet(object));
			if(hidden)
				newParameters.put(Parameter.HIDDEN, NonEmpty.set);
			if(createEvent(game, "Move " + object + ".", MOVE_OBJECTS, newParameters).attempt(event))
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
		PlayerInterface.ChooseReason reason = parameters.get(Parameter.CHOICE).getOne(PlayerInterface.ChooseReason.class);
		if(parameters.containsKey(Parameter.HIDDEN) && reason.isPublic)
			reason = new PlayerInterface.ChooseReason(reason.source, reason.query, false);
		Collection<GameObject> chosen = player.sanitizeAndChoose(game.actualState, number.getLower(0), number.getUpper(choices.size()), choices, PlayerInterface.ChoiceType.OBJECTS, reason);
		if(!number.contains(chosen.size()))
			event.allChoicesMade = false;

		event.putChoices(player, chosen);
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		MagicSet chosen = event.getChoices(parameters.get(Parameter.PLAYER).getOne(Player.class));
		boolean status = true;

		if(!chosen.isEmpty())
		{
			Map<Parameter, MagicSet> moveParameters = new HashMap<Parameter, MagicSet>(parameters);
			moveParameters.put(Parameter.CAUSE, parameters.get(Parameter.CAUSE));
			moveParameters.put(Parameter.TO, parameters.get(Parameter.TO));
			moveParameters.put(Parameter.OBJECT, chosen);
			if(parameters.containsKey(Parameter.INDEX))
				moveParameters.put(Parameter.INDEX, parameters.get(Parameter.INDEX));

			Event moveEvent = createEvent(game, "Move " + chosen + ".", MOVE_OBJECTS, moveParameters);
			status = moveEvent.perform(event, false);

			event.setResult(moveEvent.getResultGenerator());
		}
		else
			event.setResult(Empty.set);

		return event.allChoicesMade && status;
	}
}