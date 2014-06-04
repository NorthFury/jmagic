package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class MulliganSimultaneous extends EventType
{	public static final EventType INSTANCE = new MulliganSimultaneous();

	 private MulliganSimultaneous()
	{
		super("MULLIGAN_SIMULTANEOUS");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.PLAYER;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		boolean ret = true;

		List<Player> orderedPlayers = new LinkedList<Player>(game.physicalState.players);
		orderedPlayers.retainAll(parameters.get(Parameter.PLAYER).getAll(Player.class));

		Iterator<Player> iter = orderedPlayers.iterator();
		// keys are player ids, values are event ids (no entry in the map
		// means that the player chose to keep)
		Map<Integer, Integer> mulligans = new HashMap<Integer, Integer>();
		while(iter.hasNext())
		{
			Player player = iter.next();
			boolean keep = (player.getHand(game.actualState).objects.size() == 0);

			if(!keep)
			{
				Map<Parameter, MagicSet> mulliganParameters = new HashMap<Parameter, MagicSet>();
				mulliganParameters.put(EventType.Parameter.PLAYER, new MagicSet(player));
				Event mulligan = createEvent(game, "Mulligan", EventType.MULLIGAN, mulliganParameters);

				// 103.4a If an effect allows a player to perform an action
				// any time [that player] could mulligan, the player may
				// perform that action at a time he or she would declare
				// whether or not he or she will take a mulligan. ... If the
				// player performs the action, he or she then declares
				// whether or not he or she will take a mulligan.

				// [aka the Serum Powder rule]
				// We loop until the player has made a "real" keep/mulligan
				// decision, since according to 103.4a (see above) a Serum
				// Powder mulligan causes the player to then choose whether
				// to take a mulligan.
				while(!(keep || mulligans.containsKey(player.ID)))
				{
					Collection<Event> choices = new HashSet<Event>();
					choices.add(mulligan);
					for(Map.Entry<Integer, EventFactory> e: player.getActual().mulliganOptions.entrySet())
						choices.add(e.getValue().createEvent(game, game.actualState.<GameObject>get(e.getKey())));

					List<Event> chosen = player.sanitizeAndChoose(game.physicalState, 0, 1, choices, PlayerInterface.ChoiceType.EVENT, PlayerInterface.ChooseReason.MULLIGAN);

					if(chosen.isEmpty())
						keep = true;
					else
					{
						Event mulliganEvent = chosen.iterator().next();
						if(mulliganEvent.equals(mulligan))
							mulligans.put(player.ID, mulliganEvent.ID);
						else
							mulliganEvent.perform(null, true);
					}
				}
			}

			if(keep)
			{
				iter.remove();
				ret = false;
			}
		}

		// Players that chose to keep are not in the list anymore.
		for(Player player: orderedPlayers)
			game.physicalState.<Event>get(mulligans.get(player.ID)).perform(event, false);

		event.setResult(Identity.instance(orderedPlayers));
		return ret;
	}
}