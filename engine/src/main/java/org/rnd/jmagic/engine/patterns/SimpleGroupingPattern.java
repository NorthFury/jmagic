package org.rnd.jmagic.engine.patterns;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashSet;
import java.util.Set;

public class SimpleGroupingPattern implements GroupingPattern
{
	private final SetGenerator generator;

	public SimpleGroupingPattern(SetGenerator generator)
	{
		this.generator = generator;
	}

	@Override
	public Set<MagicSet> match(MagicSet set, Identified thisObject, GameState state)
	{
		Set<MagicSet> ret = new HashSet<MagicSet>();

		for(Object object: Intersect.instance(this.generator, Identity.instance(set)).evaluate(state, thisObject))
			ret.add(new MagicSet(object));

		return ret;
	}
}
