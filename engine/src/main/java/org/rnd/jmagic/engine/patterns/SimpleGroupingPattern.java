package org.rnd.jmagic.engine.patterns;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

public class SimpleGroupingPattern implements GroupingPattern
{
	private final SetGenerator generator;

	public SimpleGroupingPattern(SetGenerator generator)
	{
		this.generator = generator;
	}

	@Override
	public java.util.Set<MagicSet> match(MagicSet set, Identified thisObject, GameState state)
	{
		java.util.Set<MagicSet> ret = new java.util.HashSet<MagicSet>();

		for(Object object: Intersect.instance(this.generator, Identity.instance(set)).evaluate(state, thisObject))
			ret.add(new MagicSet(object));

		return ret;
	}
}
