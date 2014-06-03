package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

/**
 * Evaluates to all the members of all the collections in the given set. Useful
 * after a player has chosen a pile (like for Fact or Fiction) since the pile
 * will be a Collection inside the Set.
 */
public class ExplodeCollections extends SetGenerator
{
	private SetGenerator x;

	private ExplodeCollections(SetGenerator x)
	{
		this.x = x;
	}

	public static SetGenerator instance(SetGenerator x)
	{
		return new ExplodeCollections(x);
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet x = this.x.evaluate(state, thisObject);
		MagicSet ret = new MagicSet();
		for(java.util.Collection<?> c: x.getAll(java.util.Collection.class))
			ret.addAll(c);
		return ret;
	}

}
