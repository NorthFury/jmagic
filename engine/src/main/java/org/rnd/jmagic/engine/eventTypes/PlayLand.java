package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.Map;

public final class PlayLand extends EventType
{	public static final EventType INSTANCE = new PlayLand();

	 private PlayLand()
	{
		super("PLAY_LAND");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.LAND;
	}

	@Override
	public boolean attempt(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		Player player = parameters.get(Parameter.PLAYER).getOne(Player.class);
		Turn currentTurn = game.actualState.currentTurn();

		if(!(player.ID == currentTurn.ownerID))
			return false;

		return parameters.containsKey(Parameter.ACTION) && parameters.get(Parameter.ACTION).getOne(PlayLandAction.class) != null;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		GameObject land = parameters.get(Parameter.LAND).getOne(GameObject.class);
		Player player = parameters.get(Parameter.PLAYER).getOne(Player.class);
		if(player.ID != game.actualState.currentTurn().ownerID)
		{
			event.setResult(Empty.set);
			return false;
		}

		PlayLandAction action = parameters.containsKey(EventType.Parameter.ACTION) ? parameters.get(EventType.Parameter.ACTION).getOne(PlayLandAction.class) : null;

		if(action == null)
		{
			event.setResult(Empty.set);
			return false;
		}

		Map<Parameter, MagicSet> playParameters = new HashMap<Parameter, MagicSet>();
		playParameters.put(Parameter.CAUSE, new MagicSet(game));
		playParameters.put(Parameter.CONTROLLER, new MagicSet(player));
		playParameters.put(Parameter.OBJECT, new MagicSet(land));
		Event putOntoBattlefield = createEvent(game, player + " puts " + land + " onto the battlefield.", PUT_ONTO_BATTLEFIELD, playParameters);
		putOntoBattlefield.perform(event, true);

		GameObject playedLand = game.actualState.get(putOntoBattlefield.getResult().getOne(ZoneChange.class).newObjectID);

		Map<Parameter, MagicSet> playFlagParameters = new HashMap<Parameter, MagicSet>();
		playFlagParameters.put(Parameter.PLAYER, new MagicSet(player));
		playFlagParameters.put(Parameter.OBJECT, new MagicSet(playedLand));
		createEvent(game, player + " plays " + land + ".", BECOMES_PLAYED, playFlagParameters).perform(event, false);

		event.setResult(putOntoBattlefield.getResultGenerator());
		return true;
	}
}