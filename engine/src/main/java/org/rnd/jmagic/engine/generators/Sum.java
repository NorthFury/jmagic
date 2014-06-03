package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

/**
 * Evaluates to the sum of all numbers in a set
 */
public class Sum extends SetGenerator
{
	public static Sum instance(SetGenerator what)
	{
		return new Sum(what);
	}

	static public int get(MagicSet set)
	{
		int ret = 0;
		for(Integer n: set.getAll(Integer.class))
			ret += n;
		return ret;
	}

	public final SetGenerator numbers;

	private Sum(SetGenerator numbers)
	{
		this.numbers = numbers;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		return new MagicSet(Sum.get(this.numbers.evaluate(state, thisObject)));
	}

}
