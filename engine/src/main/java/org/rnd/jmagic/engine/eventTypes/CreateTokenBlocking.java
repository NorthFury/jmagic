package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public final class CreateTokenBlocking extends EventType
{	public static final EventType INSTANCE = new CreateTokenBlocking();

	 private CreateTokenBlocking()
	{
		super("CREATE_TOKEN_BLOCKING");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.OBJECT;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		Event putOntoBattlefield = createEvent(game, "Put a token onto the battlefield.", CREATE_TOKEN_ON_BATTLEFIELD, parameters);
		boolean status = putOntoBattlefield.perform(event, true);

		Set<GameObject> attackers = parameters.get(Parameter.ATTACKER).getAll(GameObject.class);

		for(GameObject blocker: putOntoBattlefield.getResult().getAll(GameObject.class))
			if(blocker.zoneID == game.actualState.battlefield().ID)
			{
				GameObject physicalBlocker = blocker.getPhysical();
				for(GameObject attacker: attackers)
				{
					GameObject physicalAttacker = attacker.getPhysical();
					if(physicalAttacker.getBlockedByIDs() == null)
						physicalAttacker.setBlockedByIDs(new LinkedList<Integer>());
					physicalAttacker.getBlockedByIDs().add(blocker.ID);
					physicalBlocker.getBlockingIDs().add(attacker.ID);
				}
			}

		event.setResult(putOntoBattlefield.getResultGenerator());
		return status;
	}
}