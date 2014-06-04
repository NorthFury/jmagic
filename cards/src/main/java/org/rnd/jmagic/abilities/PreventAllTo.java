package org.rnd.jmagic.abilities;

import org.rnd.jmagic.engine.*;

import java.util.Collections;
import java.util.List;

public final class PreventAllTo extends DamageReplacementEffect
{
	private final SetGenerator to;
	private final boolean combatDamageOnly;

	public PreventAllTo(Game game, SetGenerator to, String toDescription)
	{
		this(game, to, toDescription, false);
	}

	public PreventAllTo(Game game, SetGenerator to, String toDescription, boolean combatDamageOnly)
	{
		super(game, "Prevent all damage that would be dealt to " + toDescription + ".");
		this.to = to;
		this.combatDamageOnly = combatDamageOnly;
		this.makePreventionEffect();
	}

	@Override
	public DamageAssignment.Batch match(Event context, DamageAssignment.Batch damageAssignments)
	{
		DamageAssignment.Batch ret = new DamageAssignment.Batch();
		MagicSet to = this.to.evaluate(context.game, this.getSourceObject(context.state));
		for(DamageAssignment damage: damageAssignments)
			if((!this.combatDamageOnly || damage.isCombatDamage) && to.contains(context.state.get(damage.takerID)))
				ret.add(damage);
		return ret;
	}

	@Override
	public List<EventFactory> prevent(DamageAssignment.Batch damageAssignments)
	{
		damageAssignments.clear();
		return Collections.emptyList();
	}
}