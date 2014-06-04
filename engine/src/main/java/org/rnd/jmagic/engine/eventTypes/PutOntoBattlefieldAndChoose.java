package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PutOntoBattlefieldAndChoose extends EventType
{	public static final EventType INSTANCE = new PutOntoBattlefieldAndChoose();

	 private PutOntoBattlefieldAndChoose()
	{
		super("PUT_ONTO_BATTLEFIELD_AND_CHOOSE");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.OBJECT;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		Player player = parameters.get(Parameter.CONTROLLER).getOne(Player.class);
		int number = parameters.get(Parameter.NUMBER).getOne(Integer.class);
		MagicSet choices = parameters.get(Parameter.CHOICE);
		PlayerInterface.ChoiceType type = parameters.get(Parameter.TYPE).getOne(PlayerInterface.ChoiceType.class);
		PlayerInterface.ChooseReason description = parameters.get(Parameter.TYPE).getOne(PlayerInterface.ChooseReason.class);
		List<Object> result = player.sanitizeAndChoose(game.actualState, number, choices, type, description);

		for(Linkable link: parameters.get(Parameter.SOURCE).getAll(Linkable.class))
		{
			Linkable physicalLink = link.getPhysical();
			for(Object o: result)
			{
				physicalLink.getLinkManager().addLinkInformation(o);
				link.getLinkManager().addLinkInformation(o);
			}
		}

		Map<Parameter, MagicSet> moveParameters = new HashMap<Parameter, MagicSet>();
		moveParameters.put(Parameter.CAUSE, parameters.get(Parameter.CAUSE));
		moveParameters.put(Parameter.CONTROLLER, parameters.get(Parameter.CONTROLLER));
		moveParameters.put(Parameter.OBJECT, parameters.get(Parameter.OBJECT));
		Event moveEvent = createEvent(game, "Put " + parameters.get(Parameter.OBJECT) + " onto the battlefield", EventType.PUT_ONTO_BATTLEFIELD, moveParameters);
		boolean ret = moveEvent.perform(event, false);

		event.setResult(moveEvent.getResultGenerator());

		return ret;
	}
}