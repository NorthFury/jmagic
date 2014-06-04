package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.LinkedList;
import java.util.Map;

public final class PlayerMayCast extends EventType
{	public static final EventType INSTANCE = new PlayerMayCast();

	 private PlayerMayCast()
	{
		super("PLAYER_MAY_CAST");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.OBJECT;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		GameObject spell = parameters.get(Parameter.OBJECT).getOne(GameObject.class);
		Player player = parameters.get(Parameter.PLAYER).getOne(Player.class);

		CastSpellAction action = new CastSpellAction(game, spell, player, spell.ID);

		if(parameters.containsKey(Parameter.ALTERNATE_COST))
			action.forceAlternateCost(Identity.instance(parameters.get(Parameter.ALTERNATE_COST)));

		PlayerInterface.ChooseParameters<Answer> chooseParameters = new PlayerInterface.ChooseParameters<Answer>(1, 1, new LinkedList<Answer>(Answer.mayChoices()), PlayerInterface.ChoiceType.ENUM, PlayerInterface.ChooseReason.MAY_CAST);
		chooseParameters.thisID = spell.ID;
		Answer answer = player.choose(chooseParameters).get(0);

		if(answer == Answer.YES)
			if(action.saveStateAndPerform())
			{
				event.setResult(new MagicSet(game.actualState.get(spell.getActual().futureSelf)));
				return true;
			}

		event.setResult(Empty.set);
		return false;
	}
}