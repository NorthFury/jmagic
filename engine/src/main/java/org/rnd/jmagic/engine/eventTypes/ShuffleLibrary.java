package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

public final class ShuffleLibrary extends EventType
{	public static final EventType INSTANCE = new ShuffleLibrary();

	 private ShuffleLibrary()
	{
		super("SHUFFLE_LIBRARY");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.PLAYER;
	}

	@Override
	public boolean perform(Game game, Event event, java.util.Map<Parameter, MagicSet> parameters)
	{
		MagicSet result = new MagicSet();
		MagicSet cause = parameters.get(Parameter.CAUSE);

		for(Player player: parameters.get(Parameter.PLAYER).getAll(Player.class))
		{
			java.util.Map<Parameter, MagicSet> shuffleParameters = new java.util.HashMap<Parameter, MagicSet>();
			shuffleParameters.put(Parameter.CAUSE, cause);
			shuffleParameters.put(Parameter.PLAYER, new MagicSet(player));
			Event shuffleOne = createEvent(game, player + " shuffles their library.", EventType.SHUFFLE_ONE_LIBRARY, shuffleParameters);
			if(shuffleOne.perform(event, false))
				result.addAll(shuffleOne.getResult());
		}

		// Objects were created... :(
		game.refreshActualState();

		event.setResult(Identity.instance(result));
		return true;
	}
}