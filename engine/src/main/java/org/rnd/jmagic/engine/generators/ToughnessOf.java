package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

public class ToughnessOf extends SetGenerator
{
	public static ToughnessOf instance(SetGenerator what)
	{
		return new ToughnessOf(what);
	}

	private SetGenerator what;

	private ToughnessOf(SetGenerator what)
	{
		this.what = what;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();
		for(GameObject object: this.what.evaluate(state, thisObject).getAll(GameObject.class))
			// 207.3. A noncreature permanent has no power or toughness, even if
			// it's a card with a power and toughness printed on it.
			// I'm assuming here that a nonpermanent object isn't going to have
			// p/t if it isn't a creature card.
			if(object.getTypes().contains(Type.CREATURE))
				ret.add(object.getToughness());
		return ret;
	}

}
