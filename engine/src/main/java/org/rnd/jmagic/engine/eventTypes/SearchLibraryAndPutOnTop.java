package org.rnd.jmagic.engine.eventTypes;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.Map;

public final class SearchLibraryAndPutOnTop extends EventType
{	public static final EventType INSTANCE = new SearchLibraryAndPutOnTop();

	 private SearchLibraryAndPutOnTop()
	{
		super("SEARCH_LIBRARY_AND_PUT_ON_TOP");
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
		MagicSet player = parameters.get(Parameter.PLAYER);

		Map<Parameter, MagicSet> searchParameters = new HashMap<Parameter, MagicSet>();
		searchParameters.put(Parameter.CAUSE, cause);
		searchParameters.put(Parameter.PLAYER, player);
		searchParameters.put(Parameter.NUMBER, ONE);
		searchParameters.put(Parameter.CARD, new MagicSet(player.getOne(Player.class).getLibrary(game.actualState)));
		if(parameters.containsKey(Parameter.TYPE))
			searchParameters.put(Parameter.TYPE, parameters.get(Parameter.TYPE));
		Event search = createEvent(game, "Search your library for a card.", SEARCH, searchParameters);
		search.perform(event, false);
		MagicSet searchedFor = search.getResult();

		Map<Parameter, MagicSet> shuffleParameters = new HashMap<Parameter, MagicSet>();
		shuffleParameters.put(Parameter.CAUSE, cause);
		shuffleParameters.put(Parameter.PLAYER, player);
		createEvent(game, "Shuffle your library.", SHUFFLE_LIBRARY, shuffleParameters).perform(event, true);

		GameObject found = searchedFor.getOne(GameObject.class);

		if(found != null)
		{
			MagicSet putOnTop = new MagicSet(game.actualState.<GameObject>get(found.getPhysical().futureSelf));

			Map<Parameter, MagicSet> moveParameters = new HashMap<Parameter, MagicSet>();
			moveParameters.put(Parameter.CAUSE, cause);
			moveParameters.put(Parameter.INDEX, ONE);
			moveParameters.put(Parameter.OBJECT, putOnTop);
			createEvent(game, "Put that card on top of your library.", PUT_INTO_LIBRARY, moveParameters).perform(event, true);
		}

		event.setResult(Empty.set);
		return true;
	}
}