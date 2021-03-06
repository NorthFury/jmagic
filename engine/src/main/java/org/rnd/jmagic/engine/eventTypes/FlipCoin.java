package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public final class FlipCoin extends EventType
{	public static final EventType INSTANCE = new FlipCoin();

	 private FlipCoin()
	{
		super("FLIP_COIN");
	}

	private final Random generator = new Random();

	@Override
	public Parameter affects()
	{
		return Parameter.PLAYER;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		Answer[] possibleResults = {Answer.WIN, Answer.LOSE};
		if(parameters.containsKey(Parameter.TYPE))
		{
			Set<Answer> typeParameter = parameters.get(Parameter.TYPE).getAll(Answer.class);
			if(typeParameter.size() != 2)
				throw new UnsupportedOperationException("Coin flip type " + typeParameter + " does not contain exactly two Answer objects!");
			possibleResults = typeParameter.toArray(possibleResults);
		}

		Answer flipResult = possibleResults[this.generator.nextBoolean() ? 1 : 0];
		if(game.noRandom)
		{
			Player player = parameters.get(Parameter.PLAYER).getOne(Player.class);
			flipResult = player.choose(1, Arrays.asList(possibleResults), PlayerInterface.ChoiceType.COIN_FLIP, PlayerInterface.ChooseReason.MANIPULATE_COIN_FLIP).get(0);
		}

		event.setResult(Identity.instance(flipResult));

		// This returns false if the flip resulted in Answer.LOSE, but true
		// for everything else (including Answer.WIN, and any specified
		// Answers)
		return (flipResult != Answer.LOSE);
	}
}