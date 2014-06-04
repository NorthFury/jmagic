package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

import java.util.Set;

/**
 * Evaluates to the unique elements of the first set, compared to the second
 */
public class RelativeComplement extends SetGenerator
{
	public static RelativeComplement instance(SetGenerator a, SetGenerator b)
	{
		return new RelativeComplement(a, b);
	}

	static public MagicSet get(MagicSet a, MagicSet b)
	{
		MagicSet ret = new MagicSet(a);
		ret.removeAll(b);
		return ret;
	}

	private final SetGenerator a;
	private final SetGenerator b;

	private RelativeComplement(SetGenerator a, SetGenerator b)
	{
		this.a = a;
		this.b = b;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		return RelativeComplement.get(this.a.evaluate(state, thisObject), this.b.evaluate(state, thisObject));
	}

	@Override
	public Set<ManaSymbol.ManaType> extractColors(Game game, GameObject thisObject, Set<SetGenerator> ignoreThese) throws NoSuchMethodException
	{
		Set<ManaSymbol.ManaType> a = this.a.extractColors(game, thisObject, ignoreThese);
		Set<ManaSymbol.ManaType> b = this.b.extractColors(game, thisObject, ignoreThese);
		a.removeAll(b);
		return a;
	}
}
