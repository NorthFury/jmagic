package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class PlayerMay extends EventType
{	public static final EventType INSTANCE = new PlayerMay();

	 private PlayerMay()
	{
		super("PLAYER_MAY");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.PLAYER;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		MagicSet eventParameter = parameters.get(Parameter.EVENT);
		Event mayEvent = eventParameter.getOne(EventFactory.class).createEvent(game, event.getSource());

		Linkable link = eventParameter.getOne(Linkable.class);
		if(link != null)
			mayEvent.setStoreInObject(link);

		if(!mayEvent.attempt(event))
		{
			event.setResult(Empty.set);
			return false;
		}

		Player chooser = parameters.get(Parameter.PLAYER).getOne(Player.class);

		PlayerInterface.ChooseParameters<Answer> chooseParameters = new PlayerInterface.ChooseParameters<Answer>(1, 1, new LinkedList<Answer>(Answer.mayChoices()), PlayerInterface.ChoiceType.ENUM, PlayerInterface.ChooseReason.MAY_EVENT);
		chooseParameters.thisID = mayEvent.ID;
		if(parameters.containsKey(Parameter.TARGET))
			chooseParameters.whatForID = parameters.get(Parameter.TARGET).getOne(Identified.class).ID;

		List<Answer> choice = chooser.choose(chooseParameters);

		MagicSet result = new MagicSet();
		for(Answer response: choice)
			result.add(response);
		event.setResult(Identity.instance(result));

		if(choice.size() == 0 || choice.get(0).equals(Answer.NO))
			return false;
		return mayEvent.perform(event, false);
	}
}