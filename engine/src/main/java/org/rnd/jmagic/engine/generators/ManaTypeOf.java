package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

import java.util.Set;

public class ManaTypeOf extends SetGenerator
{
	public static ManaTypeOf instance(SetGenerator what)
	{
		return new ManaTypeOf(what);
	}

	private SetGenerator what;

	private ManaTypeOf(SetGenerator what)
	{
		this.what = what;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();
		for(ManaSymbol symbol: this.what.evaluate(state, thisObject).getAll(ManaSymbol.class))
			ret.addAll(symbol.getManaTypes());
		return ret;
	}

	@Override
	public Set<ManaSymbol.ManaType> extractColors(Game game, GameObject thisObject, Set<SetGenerator> ignoreThese) throws NoSuchMethodException
	{
		return this.evaluate(game, thisObject).getAll(ManaSymbol.ManaType.class);
	}
}
