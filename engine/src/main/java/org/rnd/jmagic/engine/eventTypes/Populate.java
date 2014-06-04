package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Populate extends EventType
{	public static final EventType INSTANCE = new Populate();

	 private Populate()
	{
		super("POPULATE");
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

		boolean ret = true;
		Set<Identified> all = Intersect.instance(Tokens.instance(), CreaturePermanents.instance(), ControlledBy.instance(Identity.instance(player))).evaluate(game, null).getAll(Identified.class);

		if(!all.isEmpty())
		{
			List<Identified> chosen = player.sanitizeAndChoose(game.actualState, 1, 1, all, PlayerInterface.ChoiceType.OBJECTS, PlayerInterface.ChooseReason.POPULATE);

			for(Identified choice: chosen)
			{
				Map<Parameter, MagicSet> copyParameters = new HashMap<Parameter, MagicSet>();
				copyParameters.put(Parameter.CAUSE, parameters.get(Parameter.CAUSE));
				copyParameters.put(Parameter.CONTROLLER, new MagicSet(player));
				copyParameters.put(Parameter.OBJECT, new MagicSet(choice));
				if(!createEvent(game, "Create a token copy of " + choice + ".", EventType.CREATE_TOKEN_COPY, copyParameters).perform(event, false))
					ret = false;
			}
		}

		event.setResult(Identity.instance());
		return ret;
	}
}