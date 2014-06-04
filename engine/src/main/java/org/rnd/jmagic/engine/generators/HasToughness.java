package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.Convenience;
import org.rnd.jmagic.engine.*;
import org.rnd.util.NumberRange;

import java.util.Set;

public class HasToughness extends SetGenerator
{
	public static HasToughness instance(int what)
	{
		return new HasToughness(Convenience.numberGenerator(what));
	}

	public static HasToughness instance(SetGenerator what)
	{
		return new HasToughness(what);
	}

	private SetGenerator what;

	private HasToughness(SetGenerator what)
	{
		this.what = what;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();
		MagicSet what = this.what.evaluate(state, thisObject);
		NumberRange range = what.getOne(NumberRange.class);
		if(range != null)
		{
			for(GameObject object: state.getAllObjects())
				// Only creatures have power
				if(object.getTypes().contains(Type.CREATURE) && range.contains(object.getToughness()))
					ret.add(object);
		}
		else
		{
			Set<Integer> numbers = what.getAll(Integer.class);
			for(GameObject object: state.getAllObjects())
				// Only creatures have power
				if(object.getTypes().contains(Type.CREATURE) && numbers.contains(object.getToughness()))
					ret.add(object);
		}
		return ret;
	}

}
