package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

public final class SearchForAllAndPutInto extends EventType
{	public static final EventType INSTANCE = new SearchForAllAndPutInto();

	 private SearchForAllAndPutInto()
	{
		super("SEARCH_FOR_ALL_AND_PUT_INTO");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.PLAYER;
	}

	@Override
	public boolean perform(Game game, Event event, java.util.Map<Parameter, MagicSet> parameters)
	{
		GameObject cause = parameters.get(Parameter.CAUSE).getOne(GameObject.class);
		Player player = parameters.get(Parameter.PLAYER).getOne(Player.class);

		java.util.Set<Zone> zones = parameters.get(Parameter.ZONE).getAll(Zone.class);
		MagicSet cards = new MagicSet();
		for(Zone zone: zones)
			cards.addAll(zone.objects);

		SetGenerator restriction = parameters.get(Parameter.TYPE).getOne(SetGenerator.class);
		MagicSet canBeFound = restriction.evaluate(game, event.getSource());

		// All cards in public zones must be chosen.
		// 400.2. ... Graveyard, battlefield, stack, exile, ante, and
		// command are public zones. ...
		MagicSet toPut = new MagicSet();
		for(GameObject card: cards.getAll(GameObject.class))
		{
			Zone zone = card.getZone();
			if(zone.isGraveyard() //
					|| game.actualState.stack().equals(zone) //
					|| game.actualState.battlefield().equals(zone) //
					|| game.actualState.exileZone().equals(zone) //
					|| game.actualState.commandZone().equals(zone)) //
			{
				if(canBeFound.contains(card))
					toPut.add(card);
				zones.remove(zone);
			}
		}

		// Cards in other zones don't need to be found, even if the entire
		// zone is revealed:
		// 400.2. ... Library and hand are hidden zones, even if all the
		// cards in one such zone happen to be revealed.
		java.util.Map<Parameter, MagicSet> searchParameters = new java.util.HashMap<Parameter, MagicSet>();
		searchParameters.put(Parameter.CAUSE, new MagicSet(cause));
		searchParameters.put(Parameter.PLAYER, new MagicSet(player));
		searchParameters.put(Parameter.NUMBER, new MagicSet(new org.rnd.util.NumberRange(0, null)));
		searchParameters.put(Parameter.CARD, new MagicSet(zones));
		searchParameters.put(Parameter.TYPE, new MagicSet(restriction));
		Event search = createEvent(game, player + " searches " + zones, EventType.SEARCH, searchParameters);
		search.perform(event, false);

		toPut.addAll(search.getResult().getAll(GameObject.class));

		MagicSet result = new MagicSet();
		Zone to = parameters.get(Parameter.TO).getOne(Zone.class);
		if(!toPut.isEmpty())
		{
			java.util.Map<EventType.Parameter, MagicSet> moveParameters = new java.util.HashMap<EventType.Parameter, MagicSet>();
			moveParameters.put(Parameter.CAUSE, new MagicSet(cause));
			moveParameters.put(Parameter.OBJECT, toPut);
			moveParameters.put(Parameter.TO, new MagicSet(to));
			Event move = createEvent(game, "Put " + toPut + " into " + to, EventType.MOVE_OBJECTS, moveParameters);
			move.perform(event, false);
			result.addAll(move.getResult());
		}

		event.setResult(Identity.instance(result));
		return true;
	}
}