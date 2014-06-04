package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

import java.util.EnumSet;
import java.util.Set;

/**
 * Evaluates to the colors of each given GameObject and ManaSymbol
 */
public class ColorsOf extends SetGenerator
{
	public static ColorsOf instance(SetGenerator what)
	{
		return new ColorsOf(what);
	}

	private final SetGenerator what;

	private ColorsOf(SetGenerator what)
	{
		this.what = what;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();

		for(GameObject object: this.what.evaluate(state, thisObject).getAll(GameObject.class))
			ret.addAll(object.getColors());
		for(ManaSymbol symbol: this.what.evaluate(state, thisObject).getAll(ManaSymbol.class))
			ret.addAll(symbol.colors);

		return ret;
	}

	@Override
	public Set<ManaSymbol.ManaType> extractColors(Game game, GameObject thisObject, Set<SetGenerator> ignoreThese) throws NoSuchMethodException
	{
		Set<ManaSymbol.ManaType> ret = EnumSet.noneOf(ManaSymbol.ManaType.class);

		for(Color color: this.evaluate(game, thisObject).getAll(Color.class))
			ret.add(color.getManaType());

		return ret;
	}
}
