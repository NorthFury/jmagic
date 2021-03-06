package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public final class BecomesBlocked extends EventType
{	public static final EventType INSTANCE = new BecomesBlocked();

	 private BecomesBlocked()
	{
		super("BECOMES_BLOCKED");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.ATTACKER;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		MagicSet attackers = parameters.get(Parameter.ATTACKER);

		for(GameObject attacker: attackers.getAll(GameObject.class))
		{
			GameObject physicalAttacker = attacker.getPhysical();
			if(physicalAttacker.getBlockedByIDs() == null)
			{
				attacker.setBlockedByIDs(new LinkedList<Integer>());
				physicalAttacker.setBlockedByIDs(new LinkedList<Integer>());
			}
		}

		for(GameObject blocker: parameters.get(Parameter.DEFENDER).getAll(GameObject.class))
		{
			MagicSet blockerSet = new MagicSet(blocker);
			Map<Parameter, MagicSet> blockedParameters = new HashMap<Parameter, MagicSet>();
			blockedParameters.put(Parameter.ATTACKER, attackers);
			blockedParameters.put(Parameter.DEFENDER, blockerSet);
			createEvent(game, blockerSet + " blocks " + attackers, EventType.BECOMES_BLOCKED_BY_ONE, blockedParameters).perform(event, false);
		}

		event.setResult(Empty.set);
		return true;
	}
}