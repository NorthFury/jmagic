package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.util.NumberRange;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ShuffleIntoLibraryChoice extends EventType
{	public static final EventType INSTANCE = new ShuffleIntoLibraryChoice();

	 private ShuffleIntoLibraryChoice()
	{
		super("SHUFFLE_INTO_LIBRARY_CHOICE");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.PLAYER;
	}

	@Override
	public boolean attempt(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		int required = getRange(parameters.get(Parameter.NUMBER)).getLower(0);
		MagicSet choices = parameters.get(Parameter.CHOICE);
		return choices.size() >= required;
	}

	@Override
	public void makeChoices(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		NumberRange number = getRange(parameters.get(Parameter.NUMBER));
		Player choosing = parameters.get(Parameter.PLAYER).getOne(Player.class);
		MagicSet choices = parameters.get(Parameter.CHOICE);

		List<Object> chosen = choosing.sanitizeAndChoose(game.actualState, number.getLower(0), number.getUpper(choices.size()), choices, PlayerInterface.ChoiceType.OBJECTS, PlayerInterface.ChooseReason.SHUFFLE_OBJECTS);
		event.allChoicesMade = (chosen.size() >= number.getLower(0));
		event.putChoices(choosing, chosen);
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		MagicSet objectsToShuffle = event.getChoices(parameters.get(Parameter.PLAYER).getOne(Player.class));
		String name = "Shuffle " + objectsToShuffle + " into " + (objectsToShuffle.size() <= 1 ? "its owner's library." : "their owners' libraries.");

		for(GameObject o: objectsToShuffle.getAll(GameObject.class))
			objectsToShuffle.add(o.getOwner(game.actualState));

		boolean ret = event.allChoicesMade;
		Map<Parameter, MagicSet> shuffleParameters = new HashMap<Parameter, MagicSet>();
		shuffleParameters.put(Parameter.CAUSE, parameters.get(Parameter.CAUSE));
		shuffleParameters.put(Parameter.OBJECT, objectsToShuffle);
		Event shuffle = createEvent(game, name, SHUFFLE_INTO_LIBRARY, shuffleParameters);
		ret = shuffle.perform(event, false) && ret;

		event.setResult(shuffle.getResultGenerator());
		return ret;
	}
}