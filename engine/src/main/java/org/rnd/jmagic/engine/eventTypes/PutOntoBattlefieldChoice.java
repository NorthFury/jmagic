package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.util.NumberNames;
import org.rnd.util.NumberRange;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class PutOntoBattlefieldChoice extends EventType
{	public static final EventType INSTANCE = new PutOntoBattlefieldChoice();

	 private PutOntoBattlefieldChoice()
	{
		super("PUT_ONTO_BATTLEFIELD_CHOICE");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.CONTROLLER;
	}

	@Override
	public boolean attempt(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		MagicSet cause = parameters.get(Parameter.CAUSE);
		MagicSet controller = parameters.get(Parameter.CONTROLLER);
		MagicSet objects = parameters.get(Parameter.OBJECT);
		int number = getRange(parameters.get(Parameter.NUMBER)).getLower(0);

		int successes = 0;
		for(GameObject object: objects.getAll(GameObject.class))
		{
			Map<Parameter, MagicSet> putOntoBattlefieldParameters = new HashMap<Parameter, MagicSet>();
			putOntoBattlefieldParameters.put(Parameter.CAUSE, cause);
			putOntoBattlefieldParameters.put(Parameter.OBJECT, new MagicSet(object));
			putOntoBattlefieldParameters.put(Parameter.CONTROLLER, controller);
			Event putOntoBattlefield = createEvent(game, "Put " + object + " onto the battlefield", PUT_ONTO_BATTLEFIELD, putOntoBattlefieldParameters);
			if(putOntoBattlefield.attempt(event))
			{
				successes++;
				if(successes == number)
					return true;
			}
		}

		return false;
	}

	@Override
	public void makeChoices(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		Set<GameObject> choices = parameters.get(Parameter.OBJECT).getAll(GameObject.class);
		Player chooser;
		if(parameters.containsKey(Parameter.PLAYER))
			chooser = parameters.get(Parameter.PLAYER).getOne(Player.class);
		else
			chooser = parameters.get(Parameter.CONTROLLER).getOne(Player.class);

		NumberRange number = getRange(parameters.get(Parameter.NUMBER));
		int lower = number.getLower(0);
		int upper = number.getUpper(choices.size());
		String choiceName;
		if(lower == upper)
		{
			if(1 == lower)
				choiceName = "Put a card onto the battlefield.";
			else
				choiceName = "Put " + NumberNames.get(lower) + " cards onto the battlefield.";
		}
		else if(lower == 0)
		{
			if(upper == 1)
				choiceName = "You may put a card onto the battlefield.";
			else
				choiceName = "Put up to " + NumberNames.get(upper) + " cards onto the battlefield.";
		}
		else
			choiceName = "Put between " + NumberNames.get(lower) + " and " + NumberNames.get(upper) + " cards onto the battlefield.";

		// offer the choices to the player
		PlayerInterface.ChooseReason reason = new PlayerInterface.ChooseReason(PlayerInterface.ChooseReason.GAME, choiceName, true);
		Collection<GameObject> chosen = chooser.sanitizeAndChoose(game.actualState, lower, upper, choices, PlayerInterface.ChoiceType.OBJECTS, reason);
		if(number.contains(chosen.size()))
			event.allChoicesMade = true;

		event.putChoices(chooser, chosen);
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		MagicSet cause = parameters.get(Parameter.CAUSE);
		Player controller = parameters.get(Parameter.CONTROLLER).getOne(Player.class);

		Player chooser;
		if(parameters.containsKey(Parameter.PLAYER))
			chooser = parameters.get(Parameter.PLAYER).getOne(Player.class);
		else
			chooser = controller;

		MagicSet stuffToPutOntoBattlefield = event.getChoices(chooser);

		Map<Parameter, MagicSet> putOntoBattlefieldParameters = new HashMap<Parameter, MagicSet>();
		putOntoBattlefieldParameters.put(Parameter.CAUSE, cause);
		putOntoBattlefieldParameters.put(Parameter.CONTROLLER, new MagicSet(controller));
		putOntoBattlefieldParameters.put(Parameter.OBJECT, stuffToPutOntoBattlefield);
		Event putOntoBattlefield = createEvent(game, "Put " + stuffToPutOntoBattlefield + " onto the battlefield", PUT_ONTO_BATTLEFIELD, putOntoBattlefieldParameters);

		boolean moveSuccess = putOntoBattlefield.perform(event, false);
		event.setResult(putOntoBattlefield.getResultGenerator());
		return event.allChoicesMade && moveSuccess;
	}

}