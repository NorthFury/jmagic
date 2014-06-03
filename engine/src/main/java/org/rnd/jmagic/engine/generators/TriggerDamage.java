package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

/**
 * Evaluates to the damage which triggered each given triggered ability
 */
public class TriggerDamage extends SetGenerator
{
	public static TriggerDamage instance(SetGenerator what)
	{
		return new TriggerDamage(what);
	}

	public static MagicSet get(EventTriggeredAbility ability)
	{
		return new MagicSet(ability.damageCause);
	}

	private final SetGenerator abilities;

	private TriggerDamage(SetGenerator abilities)
	{
		this.abilities = abilities;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();
		for(EventTriggeredAbility ability: this.abilities.evaluate(state, thisObject).getAll(EventTriggeredAbility.class))
		{
			MagicSet cause = TriggerDamage.get(ability);
			if(null != cause)
				ret.addAll(cause);
		}
		return ret;
	}

}
