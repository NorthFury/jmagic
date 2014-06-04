package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.Map;

public final class PutIntoHand extends EventType
{	public static final EventType INSTANCE = new PutIntoHand();

	 private PutIntoHand()
	{
		super("PUT_INTO_HAND");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.PERMANENT;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		MagicSet permanents = parameters.get(Parameter.PERMANENT);
		Map<Player, MagicSet> whoOwnsWhat = this.whoOwnsWhat(game.actualState, permanents);

		MagicSet result = new MagicSet();
		boolean allBounced = true;
		MagicSet cause = parameters.get(Parameter.CAUSE);
		for(Map.Entry<Player, MagicSet> ownedThings: whoOwnsWhat.entrySet())
		{
			Player owner = ownedThings.getKey();
			MagicSet thesePermanents = ownedThings.getValue();

			Map<Parameter, MagicSet> moveParameters = new HashMap<Parameter, MagicSet>();
			moveParameters.put(Parameter.CAUSE, cause);
			moveParameters.put(Parameter.TO, new MagicSet(owner.getHand(game.actualState)));
			moveParameters.put(Parameter.OBJECT, thesePermanents);
			Event move = createEvent(game, "Put " + thesePermanents + " into " + owner + "'s hand.", MOVE_OBJECTS, moveParameters);

			if(!move.perform(event, false))
				allBounced = false;
			result.addAll(move.getResult());
		}

		event.setResult(Identity.instance(result));

		return allBounced;
	}

	private Map<Player, MagicSet> whoOwnsWhat(GameState state, MagicSet permanents)
	{
		Map<Player, MagicSet> whoOwnsWhat = new HashMap<Player, MagicSet>();
		for(GameObject object: permanents.getAll(GameObject.class))
		{
			Player owner = object.getOwner(state);
			if(!whoOwnsWhat.containsKey(owner))
				whoOwnsWhat.put(owner, new MagicSet());
			whoOwnsWhat.get(owner).add(object);
		}
		return whoOwnsWhat;
	}
}