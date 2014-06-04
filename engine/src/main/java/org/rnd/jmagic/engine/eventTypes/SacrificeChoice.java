package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;
import org.rnd.util.NumberRange;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class SacrificeChoice extends EventType
{	public static final EventType INSTANCE = new SacrificeChoice();

	 private SacrificeChoice()
	{
		super("SACRIFICE_CHOICE");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.CHOICE;
	}

	@Override
	public boolean attempt(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		int required = getRange(parameters.get(Parameter.NUMBER)).getLower(0);
		if(required == 0)
			return true;

		Map<Player, MagicSet> validChoices = this.validChoices(game, event, parameters);
		for(Player p: parameters.get(Parameter.PLAYER).getAll(Player.class))
			if(!validChoices.containsKey(p) || (validChoices.get(p).size() < required))
				return false;

		return true;
	}

	@Override
	public void makeChoices(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		NumberRange numberToSac = getRange(parameters.get(Parameter.NUMBER));

		Map<Player, MagicSet> validChoices = this.validChoices(game, event, parameters);
		for(Player player: parameters.get(Parameter.PLAYER).getAll(Player.class))
		{
			if(!validChoices.containsKey(player))
			{
				event.allChoicesMade = false;
				continue;
			}
			Set<GameObject> thisPlayersStuff = validChoices.get(player).getAll(GameObject.class);
			if(thisPlayersStuff.size() < numberToSac.getLower(0))
				event.allChoicesMade = false;

			int size = thisPlayersStuff.size();
			Collection<GameObject> choices = player.sanitizeAndChoose(game.actualState, numberToSac.getLower(0), numberToSac.getUpper(size), thisPlayersStuff, PlayerInterface.ChoiceType.OBJECTS, PlayerInterface.ChooseReason.SACRIFICE);
			event.putChoices(player, choices);
		}
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		MagicSet cause = parameters.get(Parameter.CAUSE);
		MagicSet result = new MagicSet();
		boolean allSacrificed = event.allChoicesMade;

		// get the player out of the parameters and figure out what they
		// control
		for(Player player: parameters.get(Parameter.PLAYER).getAll(Player.class))
		{
			MagicSet choices = event.getChoices(player);

			Map<Parameter, MagicSet> sacParameters = new HashMap<Parameter, MagicSet>();
			sacParameters.put(Parameter.CAUSE, cause);
			sacParameters.put(Parameter.PLAYER, new MagicSet(player));
			sacParameters.put(Parameter.PERMANENT, choices);

			Event sacrifice = createEvent(game, player + " sacrifices " + choices + ".", SACRIFICE_PERMANENTS, sacParameters);
			if(!sacrifice.perform(event, false))
				allSacrificed = false;
			result.addAll(sacrifice.getResult());
		}
		event.setResult(Identity.instance(result));
		return allSacrificed;
	}

	private Map<Player, MagicSet> validChoices(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		MagicSet cause = parameters.get(Parameter.CAUSE);
		MagicSet players = parameters.get(Parameter.PLAYER);
		Map<Player, MagicSet> ret = new HashMap<Player, MagicSet>();
		for(GameObject o: parameters.get(Parameter.CHOICE).getAll(GameObject.class))
		{
			Player controller = o.getController(game.actualState);
			if(!players.contains(controller))
				continue;

			Map<Parameter, MagicSet> attemptParameters = new HashMap<Parameter, MagicSet>();
			attemptParameters.put(Parameter.CAUSE, cause);
			attemptParameters.put(Parameter.PLAYER, new MagicSet(controller));
			attemptParameters.put(Parameter.PERMANENT, new MagicSet(o));
			Event toAttempt = createEvent(game, "Sacrifice " + o, EventType.SACRIFICE_ONE_PERMANENT, attemptParameters);
			if(toAttempt.attempt(event))
			{
				if(ret.containsKey(controller))
					ret.get(controller).add(o);
				else
					ret.put(controller, new MagicSet(o));
			}
		}
		return ret;
	}
}