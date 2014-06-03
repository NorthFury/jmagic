package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

/**
 * Evaluates to all attacking, unblocked GameObjects
 */
public class Unblocked extends SetGenerator
{
	private static final Unblocked _instance = new Unblocked();

	public static Unblocked instance()
	{
		return _instance;
	}

	private Unblocked()
	{
		// Intentionally non-functional
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		if(!state.currentPhase().blockersDeclared)
			return Empty.set;

		MagicSet creatures = CreaturePermanents.instance().evaluate(state, thisObject);
		MagicSet ret = new MagicSet();

		for(GameObject object: creatures.getAll(GameObject.class))
			if(object.getAttackingID() != -1 && object.getBlockedByIDs() == null)
				ret.add(object);

		return ret;
	}
}
