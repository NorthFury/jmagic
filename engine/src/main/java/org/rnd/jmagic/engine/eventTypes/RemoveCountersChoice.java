package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.util.NumberRange;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class RemoveCountersChoice extends EventType
{	public static final EventType INSTANCE = new RemoveCountersChoice();

	 private RemoveCountersChoice()
	{
		super("REMOVE_COUNTERS_CHOICE");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.OBJECT;
	}

	@Override
	public boolean attempt(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		Set<Counter> counters = parameters.get(Parameter.COUNTER).getAll(Counter.class);

		NumberRange number = null;
		if(parameters.containsKey(Parameter.NUMBER))
			number = getRange(parameters.get(Parameter.NUMBER));
		else
			number = new NumberRange(1, 1);

		if(counters.size() < number.getLower(0))
			return false;
		return true;
	}

	@Override
	public void makeChoices(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		Player player = parameters.get(Parameter.PLAYER).getOne(Player.class);
		Set<Counter> counters = parameters.get(Parameter.COUNTER).getAll(Counter.class);
		NumberRange number = null;
		if(parameters.containsKey(Parameter.NUMBER))
			number = getRange(parameters.get(Parameter.NUMBER));
		else
			number = new NumberRange(1, 1);

		List<Counter> choice = new LinkedList<Counter>();
		if(counters.size() <= number.getLower(0))
		{
			choice.addAll(counters);
			if(counters.size() < number.getLower(0))
				event.allChoicesMade = false;
		}
		else
		{
			choice.addAll(player.choose(new PlayerInterface.ChooseParameters<Counter>(new MagicSet(number), new LinkedList<Counter>(counters), PlayerInterface.ChoiceType.STRING, PlayerInterface.ChooseReason.CHOOSE_COUNTERS)));
			event.allChoicesMade = true;
			event.putChoices(player, choice);
		}
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		Player player = parameters.get(Parameter.PLAYER).getOne(Player.class);
		MagicSet choice = event.getChoices(player);

		boolean allRemoved = true;

		MagicSet object = parameters.get(Parameter.OBJECT);
		MagicSet result = new MagicSet();
		for(Counter counter: choice.getAll(Counter.class))
		{
			Map<Parameter, MagicSet> counterParameters = new HashMap<Parameter, MagicSet>();
			counterParameters.put(Parameter.CAUSE, parameters.get(Parameter.CAUSE));
			counterParameters.put(Parameter.COUNTER, new MagicSet(counter.getType()));
			counterParameters.put(Parameter.OBJECT, new MagicSet(game.actualState.get(counter.sourceID)));
			Event removeCounter = createEvent(game, "Remove a " + counter + " from " + object + ".", EventType.REMOVE_ONE_COUNTER, counterParameters);
			boolean status = removeCounter.perform(event, false);
			if(!status)
				allRemoved = false;
			result.addAll(removeCounter.getResult());
		}
		event.setResult(result);

		return allRemoved;
	}
}