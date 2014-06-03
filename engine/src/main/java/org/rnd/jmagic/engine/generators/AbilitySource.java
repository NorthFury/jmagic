package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

/**
 * Evaluates to the source of each ability, if it has one
 */
public class AbilitySource extends SetGenerator
{
	public static AbilitySource instance(SetGenerator what)
	{
		return new AbilitySource(what);
	}

	private final SetGenerator abilities;

	private AbilitySource(SetGenerator abilities)
	{
		this.abilities = abilities;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();
		MagicSet abilities = this.abilities.evaluate(state, thisObject);
		for(NonStaticAbility ability: abilities.getAll(NonStaticAbility.class))
			ret.add(ability.getSource(state));
		for(StaticAbility ability: abilities.getAll(StaticAbility.class))
			ret.add(ability.getSource(state));
		return ret;
	}
}
