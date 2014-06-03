package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

/**
 * Evaluates to the takers of each given damage assignment
 */
public class TakerOfDamage extends SetGenerator
{
	public static TakerOfDamage instance(SetGenerator what)
	{
		return new TakerOfDamage(what);
	}

	private final SetGenerator damage;

	private TakerOfDamage(SetGenerator damage)
	{
		this.damage = damage;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();
		for(DamageAssignment assignment: this.damage.evaluate(state, thisObject).getAll(DamageAssignment.class))
			ret.add(state.get(assignment.takerID));
		return ret;
	}
}
