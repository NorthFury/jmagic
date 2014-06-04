package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PutOntoBattlefieldChoiceAttachedTo extends EventType
{	public static final EventType INSTANCE = new PutOntoBattlefieldChoiceAttachedTo();

	 private PutOntoBattlefieldChoiceAttachedTo()
	{
		super("PUT_ONTO_BATTLEFIELD_CHOICE_ATTACHED_TO");
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
		int number = 1;
		if(parameters.containsKey(Parameter.NUMBER))
			number = Sum.get(parameters.get(Parameter.NUMBER));
		MagicSet target = parameters.get(Parameter.TARGET);

		int successes = 0;
		for(GameObject object: objects.getAll(GameObject.class))
		{
			Map<Parameter, MagicSet> putOntoBattlefieldParameters = new HashMap<Parameter, MagicSet>();
			putOntoBattlefieldParameters.put(Parameter.CAUSE, cause);
			putOntoBattlefieldParameters.put(Parameter.OBJECT, new MagicSet(object));
			putOntoBattlefieldParameters.put(Parameter.CONTROLLER, controller);
			putOntoBattlefieldParameters.put(Parameter.TARGET, target);
			Event putOntoBattlefield = createEvent(game, "Put " + object + " onto the battlefield attached to " + target, PUT_ONTO_BATTLEFIELD_ATTACHED_TO, putOntoBattlefieldParameters);
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
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		MagicSet cause = parameters.get(Parameter.CAUSE);
		Player controller = parameters.get(Parameter.CONTROLLER).getOne(Player.class);
		MagicSet objects = parameters.get(Parameter.OBJECT);
		int number = 1;
		if(parameters.containsKey(Parameter.NUMBER))
			number = Sum.get(parameters.get(Parameter.NUMBER));
		Player chooser = controller;
		if(parameters.containsKey(Parameter.PLAYER))
			chooser = parameters.get(Parameter.PLAYER).getOne(Player.class);

		// offer the choices to the player
		List<GameObject> choices = chooser.sanitizeAndChoose(game.actualState, number, objects.getAll(GameObject.class), PlayerInterface.ChoiceType.OBJECTS, PlayerInterface.ChooseReason.PUT_ONTO_BATTLEFIELD);
		if(choices.size() == 0)
			return false;

		boolean allChosen = true;
		if(choices.size() != number)
			allChosen = false;

		MagicSet stuffToPutOntoBattlefield = new MagicSet(choices);
		MagicSet target = parameters.get(Parameter.TARGET);

		Map<Parameter, MagicSet> putOntoBattlefieldParameters = new HashMap<Parameter, MagicSet>();
		putOntoBattlefieldParameters.put(Parameter.CAUSE, cause);
		putOntoBattlefieldParameters.put(Parameter.CONTROLLER, new MagicSet(controller));
		putOntoBattlefieldParameters.put(Parameter.OBJECT, stuffToPutOntoBattlefield);
		putOntoBattlefieldParameters.put(Parameter.TARGET, target);
		Event putOntoBattlefield = createEvent(game, "Put " + stuffToPutOntoBattlefield + " onto the battlefield attached to " + target, PUT_ONTO_BATTLEFIELD_ATTACHED_TO, putOntoBattlefieldParameters);

		boolean moveSuccess = putOntoBattlefield.perform(event, false);
		event.setResult(putOntoBattlefield.getResultGenerator());
		return allChosen && moveSuccess;
	}
}