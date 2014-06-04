package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;
import org.rnd.util.NumberRange;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class RevealChoice extends EventType
{	public static final EventType INSTANCE = new RevealChoice();

	 private RevealChoice()
	{
		super("REVEAL_CHOICE");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.OBJECT;
	}

	@Override
	public boolean attempt(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		int number = 1;
		if(parameters.containsKey(Parameter.NUMBER))
			number = Sum.get(parameters.get(Parameter.NUMBER));
		Set<GameObject> objects = parameters.get(Parameter.OBJECT).getAll(GameObject.class);
		return (objects.size() >= number);
	}

	@Override
	public void makeChoices(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		Set<GameObject> objects = parameters.get(Parameter.OBJECT).getAll(GameObject.class);
		NumberRange number;
		if(parameters.containsKey(Parameter.NUMBER))
			number = getRange(parameters.get(Parameter.NUMBER));
		else
			number = new NumberRange(1, 1);

		Player chooser = parameters.get(Parameter.PLAYER).getOne(Player.class);
		List<GameObject> chosen = chooser.sanitizeAndChoose(game.actualState, number.getLower(0), number.getUpper(objects.size()), objects, PlayerInterface.ChoiceType.OBJECTS, PlayerInterface.ChooseReason.CHOOSE_CARD_TO_REVEAL);
		if(!number.contains(chosen.size()))
			event.allChoicesMade = false;
		event.putChoices(chooser, chosen);
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		MagicSet cause = parameters.get(Parameter.CAUSE);
		MagicSet choices = event.getChoices(parameters.get(Parameter.PLAYER).getOne(Player.class));

		Map<Parameter, MagicSet> revealParameters = new HashMap<Parameter, MagicSet>();
		revealParameters.put(Parameter.CAUSE, cause);
		revealParameters.put(Parameter.OBJECT, choices);
		if(parameters.containsKey(Parameter.EFFECT))
			revealParameters.put(Parameter.EFFECT, parameters.get(Parameter.EFFECT));
		Event revealEvent = createEvent(game, "Reveal " + choices, EventType.REVEAL, revealParameters);

		boolean ret = revealEvent.perform(event, false);

		event.setResult(revealEvent.getResultGenerator());

		return ret;
	}
}