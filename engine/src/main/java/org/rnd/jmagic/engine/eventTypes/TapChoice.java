package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.util.NumberRange;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class TapChoice extends EventType
{	public static final EventType INSTANCE = new TapChoice();

	 private TapChoice()
	{
		super("TAP_CHOICE");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.CHOICE;
	}

	@Override
	public boolean attempt(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		MagicSet choices = parameters.get(Parameter.CHOICE);
		int required = getRange(parameters.get(Parameter.NUMBER)).getLower(0);

		int tappable = 0;
		for(GameObject choice: choices.getAll(GameObject.class))
			if(!choice.isTapped())
			{
				tappable++;
				if(tappable >= required)
					return true;
			}

		return false;
	}

	@Override
	public void makeChoices(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		Player player = parameters.get(Parameter.PLAYER).getOne(Player.class);

		Set<GameObject> tappable = parameters.get(Parameter.CHOICE).getAll(GameObject.class);
		Set<GameObject> toRemove = new HashSet<GameObject>();
		for(GameObject o: tappable)
			if(o.isTapped())
				toRemove.add(o);
		tappable.removeAll(toRemove);

		NumberRange range = getRange(parameters.get(Parameter.NUMBER));

		if(range.getLower(0) > tappable.size())
			event.allChoicesMade = false;
		List<GameObject> chosen = player.sanitizeAndChoose(game.actualState, range.getLower(0), range.getUpper(tappable.size()), tappable, PlayerInterface.ChoiceType.OBJECTS, PlayerInterface.ChooseReason.TAP);

		event.putChoices(player, chosen);
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		boolean ret = event.allChoicesMade;
		MagicSet objects = event.getChoices(parameters.get(Parameter.PLAYER).getOne(Player.class));

		Map<Parameter, MagicSet> tapParameters = new HashMap<Parameter, MagicSet>();
		tapParameters.put(Parameter.CAUSE, parameters.get(Parameter.CAUSE));
		tapParameters.put(Parameter.OBJECT, objects);
		Event tap = createEvent(game, "Tap " + objects, EventType.TAP_PERMANENTS, tapParameters);
		ret = tap.perform(event, false) && ret;

		event.setResult(tap.getResultGenerator());
		return ret;
	}

}