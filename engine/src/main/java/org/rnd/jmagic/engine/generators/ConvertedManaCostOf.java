package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

/**
 * Evaluates to the converted mana cost of each given GameObject. Since Set
 * supports duplicity amongst Integers, the size of this set will be the number
 * of GameObjects passed to it.
 */
public class ConvertedManaCostOf extends SetGenerator
{
	public static ConvertedManaCostOf instance(SetGenerator what)
	{
		return new ConvertedManaCostOf(what);
	}

	private final SetGenerator what;

	private ConvertedManaCostOf(SetGenerator what)
	{
		this.what = what;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();
		for(GameObject object: this.what.evaluate(state, thisObject).getAll(GameObject.class))
			ret.add(object.getConvertedManaCost());
		return ret;
	}

}
