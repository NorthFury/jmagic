package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;
import org.rnd.util.NumberRange;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public final class PlayerChoose extends EventType
{	public static final EventType INSTANCE = new PlayerChoose();

	 private PlayerChoose()
	{
		super("PLAYER_CHOOSE");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.PLAYER;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		Player player = parameters.get(Parameter.PLAYER).getOne(Player.class);
		MagicSet choices = parameters.get(Parameter.CHOICE);
		NumberRange number = getRange(parameters.get(Parameter.NUMBER));
		PlayerInterface.ChoiceType type = parameters.get(Parameter.TYPE).getOne(PlayerInterface.ChoiceType.class);
		PlayerInterface.ChooseReason reason = parameters.get(Parameter.TYPE).getOne(PlayerInterface.ChooseReason.class);

		PlayerInterface.ChooseParameters<Serializable> chooseParameters;
		chooseParameters = new PlayerInterface.ChooseParameters<Serializable>(number.getLower(), number.getUpper(), type, reason);
		if(parameters.containsKey(Parameter.OBJECT))
			chooseParameters.thisID = parameters.get(Parameter.OBJECT).getOne(GameObject.class).ID;

		MagicSet result = new MagicSet();
		List<Object> choice = player.sanitizeAndChoose(game.actualState, choices, chooseParameters);

		if(parameters.containsKey(Parameter.ORDERED))
			result.add(choice);
		else
			result.addAll(choice);

		event.setResult(Identity.instance(result));
		return true;
	}
}