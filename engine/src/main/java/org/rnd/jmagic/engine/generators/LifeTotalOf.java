package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

/**
 * Evaluates to a set containing the life totals of each given player.
 */
public class LifeTotalOf extends SetGenerator
{
	public static LifeTotalOf instance(SetGenerator what)
	{
		return new LifeTotalOf(what);
	}

	private SetGenerator what;

	private LifeTotalOf(SetGenerator what)
	{
		this.what = what;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();
		for(Player player: this.what.evaluate(state, thisObject).getAll(Player.class))
			ret.add(player.lifeTotal);
		return ret;
	}

}
