package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;
import org.rnd.util.NumberRange;

import java.util.Set;

/**
 * Evaluates to each GameObject whose mana cost is either given, or contained in
 * the given NumberRange
 */
public class HasConvertedManaCost extends SetGenerator
{
	public static HasConvertedManaCost instance(SetGenerator what)
	{
		return new HasConvertedManaCost(what);
	}

	private final SetGenerator numbers;

	private HasConvertedManaCost(SetGenerator number)
	{
		this.numbers = number;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();

		Set<Integer> numbers = this.numbers.evaluate(state, thisObject).getAll(Integer.class);
		NumberRange range = this.numbers.evaluate(state, thisObject).getOne(NumberRange.class);

		for(GameObject object: state.getAllObjects())
		{
			int convertedCost = object.getConvertedManaCost();

			for(Integer n: numbers)
				if(convertedCost == n)
					ret.add(object);
			if(range != null)
				if(range.contains(convertedCost))
					ret.add(object);
		}

		return ret;
	}
}
