package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;
import org.rnd.util.NumberRange;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Search extends EventType
{	public static final EventType INSTANCE = new Search();

	 private Search()
	{
		super("SEARCH");
	}

	// SEARCH_FOR_ALL_AND_PUT_INTO breaks the restriction on CARD -- it
	// specifies cards from multiple zones. Card writers are not allowed to
	// break this restriction.
	@Override
	public Parameter affects()
	{
		return Parameter.PLAYER;
	}

	private boolean zoneIsHidden(Set<Zone> publicZones, Set<Zone> hiddenZones, Zone zone, Player player)
	{
		if(publicZones.contains(zone))
			return false;
		if(hiddenZones.contains(zone))
			return true;
		for(Integer playerID: zone.visibleTo)
			if(playerID != player.ID)
			{
				publicZones.add(zone);
				return false;
			}
		hiddenZones.add(zone);
		return true;
	}

	@Override
	public boolean attempt(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		MagicSet cards = parameters.get(Parameter.CARD);
		if(cards.isEmpty())
			return true;

		MagicSet player = parameters.get(Parameter.PLAYER);
		Set<Zone> zones = cards.getAll(Zone.class);
		for(Zone zone: zones)
		{
			Map<Parameter, MagicSet> testParameters = new HashMap<Parameter, MagicSet>();
			testParameters.put(Parameter.PLAYER, player);
			testParameters.put(Parameter.CARD, new MagicSet(zone));
			Event test = createEvent(game, "Search " + zone + "?", SEARCH_MARKER, testParameters);
			cards.remove(zone);
			if(!test.perform(event, false))
				return false;
		}
		return true;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		MagicSet cause = parameters.get(Parameter.CAUSE);
		Player player = parameters.get(Parameter.PLAYER).getOne(Player.class);

		MagicSet cards = parameters.get(Parameter.CARD);

		if(cards.isEmpty())
		{
			event.setResult(Empty.set);
			return true;
		}

		Set<Zone> zones = cards.getAll(Zone.class);
		for(Zone zone: zones)
		{
			Map<Parameter, MagicSet> testParameters = new HashMap<Parameter, MagicSet>();
			testParameters.put(Parameter.PLAYER, new MagicSet(player));
			testParameters.put(Parameter.CARD, new MagicSet(zone));
			Event test = createEvent(game, "Search " + zone + "?", SEARCH_MARKER, testParameters);
			cards.remove(zone);
			if(test.perform(event, false))
				cards.addAll(test.getResult());
		}
		zones = cards.getAll(Zone.class);

		Set<Zone> publicZones = new HashSet<Zone>();
		Set<Zone> hiddenZones = new HashSet<Zone>();
		Set<GameObject> publicObjects = new HashSet<GameObject>();
		for(Zone zone: zones)
		{
			cards.addAll(zone.objects);

			if(!zoneIsHidden(publicZones, hiddenZones, zone, player))
				publicObjects.addAll(zone.objects);
		}

		for(GameObject o: cards.getAll(GameObject.class))
		{
			Zone zone = o.getZone();
			zones.add(zone);
			if(!zoneIsHidden(publicZones, hiddenZones, zone, player))
				publicObjects.add(o);
		}
		MagicSet result = new MagicSet(zones);

		boolean multipleZones = (zones.size() > 1);

		Map<Parameter, MagicSet> lookParameters = new HashMap<Parameter, MagicSet>();
		lookParameters.put(Parameter.CAUSE, cause);
		lookParameters.put(Parameter.OBJECT, cards);
		lookParameters.put(Parameter.PLAYER, new MagicSet(player));
		Event lookEvent = createEvent(game, player + " looks at " + cards, EventType.LOOK, lookParameters);
		lookEvent.perform(event, false);

		boolean searchRestricted = false;
		if(parameters.containsKey(Parameter.TYPE))
		{
			searchRestricted = true;
			cards = Intersect.get(parameters.get(Parameter.TYPE).getOne(SetGenerator.class).evaluate(game, cause.getOne(Identified.class)), cards);
			publicObjects.retainAll(cards);
		}

		NumberRange num = getRange(parameters.get(Parameter.NUMBER));
		// if we have multiple zones, all of them are hidden (this event
		// will have been called from SEARCH_FOR_ALL_AND_PUT_INTO which only
		// passes hidden zones)
		Integer min = null;
		if(searchRestricted || multipleZones)
		{
			// If every zone is hidden then the minimum is zero.
			if(publicZones.isEmpty())
				num = new NumberRange(0, num.getUpper());
			else
			{
				min = num.getLower();
				if(min != null && min.intValue() > publicObjects.size())
					num = new NumberRange(publicObjects.size(), num.getUpper());
			}
		}

		List<GameObject> choices = null;
		PlayerInterface.ChooseReason reason = new PlayerInterface.ChooseReason(PlayerInterface.ChooseReason.GAME, event.getName(), false);

		int lower = num.getLower(0);
		int upper = num.getUpper(cards.size());

		boolean valid = false;
		do
		{
			choices = player.sanitizeAndChoose(game.actualState, lower, upper, cards.getAll(GameObject.class), PlayerInterface.ChoiceType.OBJECTS, reason);

			valid = true;

			// In the situation where you're searching both hidden and
			// public zones (min is not null), and you choose less than the
			// originally requested minimum (choices.size() < min), then
			// force the player to rechoose if they did not pick all
			// available public objects.
			if(min != null && choices.size() < min.intValue())
			{
				if(!choices.containsAll(publicObjects))
					valid = false;
			}
		}
		while(!valid);

		result.addAll(choices);
		if(searchRestricted)
		{
			Map<EventType.Parameter, MagicSet> revealParameters = new HashMap<EventType.Parameter, MagicSet>();
			revealParameters.put(Parameter.CAUSE, new MagicSet(cause));
			revealParameters.put(Parameter.OBJECT, new MagicSet(choices));
			createEvent(game, "Reveal " + choices, EventType.REVEAL, revealParameters).perform(event, false);
		}

		event.setResult(Identity.instance(result));

		return true;
	}
}