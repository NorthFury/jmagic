package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;
import org.rnd.util.NumberRange;

import java.util.Set;

/**
 * Evaluates to the common elements between each of the given sets
 */
public class Intersect extends SetGenerator
{
	public static Intersect instance(SetGenerator left, SetGenerator right, SetGenerator... generators)
	{
		Intersect ret = new Intersect(left, right);
		for(int i = 0; i < generators.length; ++i)
			ret = new Intersect(ret, generators[i]);

		return ret;
	}

	static public MagicSet get(MagicSet a, MagicSet b)
	{
		MagicSet ret = new MagicSet(a);
		ret.retainAll(b);

		Set<Integer> bNumbers = b.getAll(Integer.class);
		Set<NumberRange> bRanges = b.getAll(NumberRange.class);
		for(NumberRange aRange: a.getAll(NumberRange.class))
		{
			Integer aLower = aRange.getLower();
			Integer aUpper = aRange.getUpper();

			for(NumberRange bRange: bRanges)
			{
				Integer bLower = bRange.getLower();
				Integer bUpper = bRange.getUpper();

				if((aLower == null || bUpper == null || aLower <= bUpper) && (bLower == null || aUpper == null || bLower <= aUpper))
				{
					Integer newLower;
					Integer newUpper;

					if(null == aLower)
						newLower = bLower;
					else if(null == bLower)
						newLower = aLower;
					else
						newLower = Math.max(aLower, bLower);

					if(null == aUpper)
						newUpper = bUpper;
					else if(null == bUpper)
						newUpper = aUpper;
					else
						newUpper = Math.min(aUpper, bUpper);

					ret.add(new NumberRange(newLower, newUpper));
				}
			}
			for(Integer bNumber: bNumbers)
				if((null == aLower || aLower <= bNumber) && (null == aUpper || bNumber <= aUpper))
					ret.add(bNumber);
		}
		for(Integer aNumber: a.getAll(Integer.class))
		{
			for(NumberRange bRange: bRanges)
				if((null == bRange.getLower() || bRange.getLower() <= aNumber) && (null == bRange.getUpper() || aNumber <= bRange.getUpper()))
					ret.add(aNumber);
			for(Integer bNumber: bNumbers)
				if(aNumber.equals(bNumber))
					ret.add(bNumber);
		}
		return ret;
	}

	private final SetGenerator a;
	private final SetGenerator b;

	private Intersect(SetGenerator a, SetGenerator b)
	{
		this.a = a;
		this.b = b;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		return Intersect.get(this.a.evaluate(state, thisObject), this.b.evaluate(state, thisObject));
	}
}
