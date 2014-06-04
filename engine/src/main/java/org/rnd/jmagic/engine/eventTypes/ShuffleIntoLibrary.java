package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class ShuffleIntoLibrary extends EventType
{	public static final EventType INSTANCE = new ShuffleIntoLibrary();

	 private ShuffleIntoLibrary()
	{
		super("SHUFFLE_INTO_LIBRARY");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.OBJECT;
	}

	@Override
	public boolean attempt(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		for(GameObject object: parameters.get(Parameter.OBJECT).getAll(GameObject.class))
			if(object.isGhost())
				return false;
		return true;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		MagicSet cause = parameters.get(Parameter.CAUSE);
		Set<Player> player = parameters.get(Parameter.OBJECT).getAll(Player.class);

		Map<Parameter, MagicSet> moveParameters = new HashMap<Parameter, MagicSet>();
		Set<GameObject> cards = parameters.get(Parameter.OBJECT).getAll(GameObject.class);
		moveParameters.put(Parameter.CAUSE, cause);
		moveParameters.put(Parameter.INDEX, new MagicSet(-1));
		// if this is a stacked game, the cards will stay on the bottom and
		// be player-ordered
		if(!game.noRandom)
			moveParameters.put(Parameter.RANDOM, Empty.set);
		moveParameters.put(Parameter.OBJECT, new MagicSet(cards));
		Event move = createEvent(game, "Put " + cards + " into their owners' libraries.", PUT_INTO_LIBRARY, moveParameters);
		boolean moveStatus = move.perform(event, true);

		// 701.15d If an effect would cause a player to shuffle one or more
		// specific objects into a library, and a replacement or prevention
		// effect causes all such objects to be moved to another zone
		// instead, that library isn't shuffled.
		out: while(true)
		{
			// 701.15e If an effect would cause a player to shuffle a set of
			// objects into a library, that library is shuffled even if
			// there are no objects in that set.
			if(cards.isEmpty())
				break out;

			// keys are cards, values are the libraries those cards should
			// go to
			Map<Integer, Integer> expectedDestinations = new HashMap<Integer, Integer>();
			for(GameObject card: cards)
				expectedDestinations.put(card.ID, card.getOwner(game.actualState).getLibrary(game.actualState).ID);

			for(ZoneChange zc: move.getResult().getAll(ZoneChange.class))
				if(expectedDestinations.get(zc.oldObjectID) == zc.destinationZoneID)
					break out;
			event.setResult(new MagicSet());
			return false;
		}

		Map<Parameter, MagicSet> shuffleParameters = new HashMap<Parameter, MagicSet>();
		shuffleParameters.put(Parameter.CAUSE, cause);
		shuffleParameters.put(Parameter.PLAYER, new MagicSet(player));
		Event shuffle = createEvent(game, player + " shuffles their library.", SHUFFLE_LIBRARY, shuffleParameters);
		boolean shuffleStatus = shuffle.perform(event, false);

		MagicSet result = new MagicSet();
		result.addAll(move.getResult());
		result.addAll(shuffle.getResult());
		event.setResult(result);
		return moveStatus && shuffleStatus;
	}
}