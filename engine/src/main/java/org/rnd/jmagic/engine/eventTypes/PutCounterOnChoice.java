package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class PutCounterOnChoice extends EventType
{	public static final EventType INSTANCE = new PutCounterOnChoice();

	 private PutCounterOnChoice()
	{
		super("PUT_COUNTER_ON_CHOICE");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.CHOICE;
	}

	@Override
	public void makeChoices(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		int numberOfCards = 1;

		Set<GameObject> objects = parameters.get(Parameter.CHOICE).getAll(GameObject.class);

		for(Player player: parameters.get(Parameter.PLAYER).getAll(Player.class))
		{
			Collection<GameObject> choices = player.sanitizeAndChoose(game.actualState, numberOfCards, objects, PlayerInterface.ChoiceType.OBJECTS, PlayerInterface.ChooseReason.PUT_COUNTER);
			if(choices.size() != numberOfCards)
				event.allChoicesMade = false;
			event.putChoices(player, choices);
		}
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		boolean allReceivedCounters = event.allChoicesMade;
		MagicSet cause = parameters.get(Parameter.CAUSE);
		MagicSet counter = parameters.get(Parameter.COUNTER);
		MagicSet one = new MagicSet(1);
		MagicSet result = new MagicSet();

		for(Player player: parameters.get(Parameter.PLAYER).getAll(Player.class))
		{
			MagicSet putOnThese = event.getChoices(player);

			Map<Parameter, MagicSet> putCountersParameters = new HashMap<Parameter, MagicSet>();
			putCountersParameters.put(Parameter.CAUSE, cause);
			putCountersParameters.put(Parameter.COUNTER, counter);
			putCountersParameters.put(Parameter.NUMBER, one);
			putCountersParameters.put(Parameter.OBJECT, putOnThese);
			Event putCounters = createEvent(game, player + " puts a " + counter + " counter on " + putOnThese + ".", PUT_COUNTERS, putCountersParameters);
			if(!putCounters.perform(event, false))
				allReceivedCounters = false;
			result.addAll(putCounters.getResult());
		}

		event.setResult(result);
		return allReceivedCounters;
	}
}