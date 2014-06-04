package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Evaluates to everything any of the specified objects, if they're
 * fortifications, are fortified by.
 */
public class FortifiedBy extends SetGenerator
{
	public static FortifiedBy instance(SetGenerator what)
	{
		return new FortifiedBy(what);
	}

	private final SetGenerator what;

	private FortifiedBy(SetGenerator what)
	{
		this.what = what;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		Set<Integer> attachments = new HashSet<Integer>();
		for(GameObject o: this.what.evaluate(state, thisObject).getAll(GameObject.class))
			if(o.getSubTypes().contains(SubType.FORTIFICATION) && (-1 != o.getAttachedTo()))
				attachments.add(o.getAttachedTo());
		return IdentifiedWithID.instance(attachments).evaluate(state, thisObject);
	}
}
