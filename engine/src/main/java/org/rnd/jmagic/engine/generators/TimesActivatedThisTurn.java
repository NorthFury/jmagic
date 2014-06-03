package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

/**
 * Evaluates to the source of each ability, if it has one
 */
public class TimesActivatedThisTurn extends SetGenerator
{
	public static TimesActivatedThisTurn instance(SetGenerator what)
	{
		return new TimesActivatedThisTurn(what);
	}

	private final SetGenerator abilities;

	private TimesActivatedThisTurn(SetGenerator abilities)
	{
		this.abilities = abilities;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();
		MagicSet abilities = this.abilities.evaluate(state, thisObject);
		for(ActivatedAbility ability: abilities.getAll(ActivatedAbility.class))
			ret.add(ability.timesActivatedThisTurn());
		return ret;
	}
}
