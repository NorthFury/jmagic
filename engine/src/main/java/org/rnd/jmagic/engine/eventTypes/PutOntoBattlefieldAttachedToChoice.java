package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PutOntoBattlefieldAttachedToChoice extends EventType
{	public static final EventType INSTANCE = new PutOntoBattlefieldAttachedToChoice();

	 private PutOntoBattlefieldAttachedToChoice()
	{
		super("PUT_ONTO_BATTLEFIELD_ATTACHED_TO_CHOICE");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.OBJECT;
	}

	@Override
	public boolean attempt(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		MagicSet cause = parameters.get(Parameter.CAUSE);
		GameObject object = parameters.get(Parameter.OBJECT).getOne(GameObject.class);
		MagicSet controller = parameters.get(Parameter.CONTROLLER);

		for(GameObject choice: parameters.get(Parameter.CHOICE).getAll(GameObject.class))
		{
			Map<Parameter, MagicSet> putOntoBattlefieldParameters = new HashMap<Parameter, MagicSet>();
			putOntoBattlefieldParameters.put(Parameter.CAUSE, cause);
			putOntoBattlefieldParameters.put(Parameter.OBJECT, new MagicSet(object));
			putOntoBattlefieldParameters.put(Parameter.CONTROLLER, controller);
			putOntoBattlefieldParameters.put(Parameter.TARGET, new MagicSet(choice));
			Event putOntoBattlefield = createEvent(game, "Put " + object + " onto the battlefield attached to " + choice, PUT_ONTO_BATTLEFIELD_ATTACHED_TO, putOntoBattlefieldParameters);
			if(putOntoBattlefield.attempt(event))
				return true;
		}
		return false;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		MagicSet cause = parameters.get(Parameter.CAUSE);
		GameObject object = parameters.get(Parameter.OBJECT).getOne(GameObject.class);
		MagicSet controller = parameters.get(Parameter.CONTROLLER);
		MagicSet chooser = controller;
		if(parameters.containsKey(Parameter.PLAYER))
			chooser = parameters.get(Parameter.PLAYER);
		MagicSet choices = parameters.get(Parameter.CHOICE);

		List<?> chosen = chooser.getOne(Player.class).sanitizeAndChoose(game.actualState, 1, choices, PlayerInterface.ChoiceType.OBJECTS, PlayerInterface.ChooseReason.ATTACH);

		Map<Parameter, MagicSet> putOntoBattlefieldParameters = new HashMap<Parameter, MagicSet>();
		putOntoBattlefieldParameters.put(Parameter.CAUSE, cause);
		putOntoBattlefieldParameters.put(Parameter.OBJECT, new MagicSet(object));
		putOntoBattlefieldParameters.put(Parameter.CONTROLLER, controller);
		putOntoBattlefieldParameters.put(Parameter.TARGET, new MagicSet(chosen));
		Event putOntoBattlefield = createEvent(game, "Put " + object + " onto the battlefield attached to " + chosen, PUT_ONTO_BATTLEFIELD_ATTACHED_TO, putOntoBattlefieldParameters);
		boolean status = putOntoBattlefield.perform(event, false);

		event.setResult(putOntoBattlefield.getResultGenerator());
		return status;
	}
}