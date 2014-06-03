package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

public class EmblemFilter extends SetGenerator
{
	public static EmblemFilter instance(SetGenerator what)
	{
		return new EmblemFilter(what);
	}

	private SetGenerator what;

	private EmblemFilter(SetGenerator what)
	{
		this.what = what;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		return new MagicSet(this.what.evaluate(state, thisObject).getAll(Emblem.class));
	}
}
