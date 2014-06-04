package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class RevealRandomFromHand extends EventType
{	public static final EventType INSTANCE = new RevealRandomFromHand();

	 private RevealRandomFromHand()
	{
		super("REVEAL_RANDOM_FROM_HAND");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.PLAYER;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		MagicSet result = new MagicSet();
		int number = Sum.get(parameters.get(Parameter.NUMBER));
		boolean ret = true;

		for(Player player: parameters.get(Parameter.PLAYER).getAll(Player.class))
		{
			List<GameObject> reveal = new LinkedList<GameObject>();
			List<GameObject> hand = new LinkedList<GameObject>(player.getHand(game.actualState).objects);
			Collections.shuffle(hand);

			if(hand.size() <= number)
				reveal.addAll(hand);
			else
				for(int i = 0; i < number; i++)
					reveal.add(hand.remove(0));

			if(reveal.size() < number)
				ret = false;

			Map<Parameter, MagicSet> revealParameters = new HashMap<Parameter, MagicSet>();
			revealParameters.put(EventType.Parameter.CAUSE, parameters.get(Parameter.CAUSE));
			revealParameters.put(EventType.Parameter.OBJECT, new MagicSet(reveal));
			Event revealEvent = createEvent(game, player + " reveals " + reveal, EventType.REVEAL, revealParameters);

			if(!revealEvent.perform(event, false))
				ret = false;

			result.addAll(revealEvent.getResult());
		}

		event.setResult(result);

		return ret;
	}
}