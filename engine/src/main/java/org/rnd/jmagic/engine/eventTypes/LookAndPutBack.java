package org.rnd.jmagic.engine.eventTypes;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;
import org.rnd.util.NumberNames;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class LookAndPutBack extends EventType
{	public static final EventType INSTANCE = new LookAndPutBack();

	 private LookAndPutBack()
	{
		super("LOOK_AND_PUT_BACK");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.PLAYER;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{

		MagicSet cause = parameters.get(Parameter.CAUSE);
		Player player = parameters.get(Parameter.PLAYER).getOne(Player.class);
		Player target = player;
		if(parameters.containsKey(Parameter.TARGET))
			target = parameters.get(Parameter.TARGET).getOne(Player.class);
		Zone library = target.getLibrary(game.actualState);
		int number = Sum.get(parameters.get(Parameter.NUMBER));

		boolean ret = true;
		MagicSet cards = new MagicSet();
		for(int i = 0; i < number; i++)
		{
			if(i == library.objects.size())
			{
				ret = false;
				break;
			}
			cards.add(library.objects.get(i));
		}

		Map<Parameter, MagicSet> lookParameters = new HashMap<Parameter, MagicSet>();
		lookParameters.put(Parameter.CAUSE, cause);
		lookParameters.put(EventType.Parameter.OBJECT, cards);
		lookParameters.put(EventType.Parameter.PLAYER, new MagicSet(player));
		createEvent(game, "Look at the top " + NumberNames.get(number) + " cards of your library.", EventType.LOOK, lookParameters).perform(event, false);

		// then put them back in any order.
		if(target.equals(player))
		{
			// If the player looking owns the library being looked at, we
			// can do this the easy way ...
			Map<Parameter, MagicSet> moveParameters = new HashMap<Parameter, MagicSet>();
			moveParameters.put(EventType.Parameter.CAUSE, cause);
			moveParameters.put(EventType.Parameter.INDEX, ONE);
			moveParameters.put(EventType.Parameter.OBJECT, cards);
			Event move = createEvent(game, "Put them back in any order.", EventType.PUT_INTO_LIBRARY, moveParameters);
			move.perform(event, false);
			event.setResult(move.getResultGenerator());
		}
		else
		{
			// ... otherwise we'll have to manually ask the player to order
			// the cards.
			Set<GameObject> choices = cards.getAll(GameObject.class);
			List<GameObject> ordered = player.sanitizeAndChoose(game.actualState, choices.size(), choices, PlayerInterface.ChoiceType.MOVEMENT_LIBRARY, PlayerInterface.ChooseReason.ORDER_LIBRARY_TARGET);

			MagicSet result = new MagicSet();
			for(GameObject o: ordered)
			{
				Map<Parameter, MagicSet> moveParameters = new HashMap<Parameter, MagicSet>();
				moveParameters.put(EventType.Parameter.CAUSE, cause);
				moveParameters.put(EventType.Parameter.INDEX, ONE);
				moveParameters.put(EventType.Parameter.OBJECT, new MagicSet(o));
				Event move = createEvent(game, "Put a card back.", EventType.PUT_INTO_LIBRARY, moveParameters);
				move.perform(event, true);
				result.addAll(move.getResult());
			}
			event.setResult(Identity.instance(result));
		}

		return ret;
	}
}