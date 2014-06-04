package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

import java.util.Set;

/**
 * Evaluates to all the mana in the given players' mana pools, and all the mana
 * in the given mana pools.
 */
public class ManaInPool extends SetGenerator
{
	public static ManaInPool instance(SetGenerator what)
	{
		return new ManaInPool(what);
	}

	private SetGenerator what;

	private ManaInPool(SetGenerator what)
	{
		this.what = what;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet what = this.what.evaluate(state, thisObject);
		MagicSet ret = new MagicSet();
		for(Player player: what.getAll(Player.class))
			ret.addAll(player.pool);
		for(ManaPool pool: what.getAll(ManaPool.class))
			ret.addAll(pool);
		return ret;
	}

	@Override
	public Set<ManaSymbol.ManaType> extractColors(Game game, GameObject thisObject, Set<SetGenerator> ignoreThese) throws NoSuchMethodException
	{
		return Identity.instance(this.evaluate(game, thisObject)).extractColors(game, thisObject, ignoreThese);
	}
}
