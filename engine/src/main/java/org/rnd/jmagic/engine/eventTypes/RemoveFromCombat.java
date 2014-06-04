package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

public final class RemoveFromCombat extends EventType
{	public static final EventType INSTANCE = new RemoveFromCombat();

	 private RemoveFromCombat()
	{
		super("REMOVE_FROM_COMBAT");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.OBJECT;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		MagicSet results = new MagicSet();

		MagicSet objectsAndPlayers = parameters.get(Parameter.OBJECT);
		for(GameObject o: objectsAndPlayers.getAll(GameObject.class))
		{
			if(o.isGhost())
				continue;

			GameObject physical = o.getPhysical();
			physical.setAttackingID(-1);
			physical.setBlockedByIDs(null);
			physical.setBlockingIDs(new LinkedList<Integer>());
			physical.setDefendingIDs(new HashSet<Integer>());
			results.add(physical);
		}

		for(Player p: objectsAndPlayers.getAll(Player.class))
		{
			Player physical = p.getPhysical();
			physical.defendingIDs.clear();
			results.add(physical);
		}
		event.setResult(Identity.instance(results));
		return true;
	}
}