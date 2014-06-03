package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

/**
 * Evaluates to the id of each defender for each given attacker
 */
public class AttackingID extends SetGenerator
{
	public static AttackingID instance(SetGenerator what)
	{
		return new AttackingID(what);
	}

	private final SetGenerator what;

	private AttackingID(SetGenerator what)
	{
		this.what = what;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();
		for(GameObject object: this.what.evaluate(state, thisObject).getAll(GameObject.class))
			ret.add(object.getAttackingID());
		return ret;
	}
}
