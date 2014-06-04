package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Evaluates to the object(s) with the given id(s)
 */
public class IdentifiedWithID extends SetGenerator
{
	public static IdentifiedWithID instance(int what)
	{
		return new IdentifiedWithID(what);
	}

	public static IdentifiedWithID instance(Collection<Integer> what)
	{
		return new IdentifiedWithID(what);
	}

	private final Collection<Integer> IDs;

	private IdentifiedWithID(int ID)
	{
		this.IDs = new LinkedList<Integer>();
		this.IDs.add(ID);
	}

	private IdentifiedWithID(Collection<Integer> IDs)
	{
		this.IDs = new LinkedList<Integer>(IDs);
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();
		for(Integer n: this.IDs)
			if(state.containsIdentified(n))
				ret.add(state.get(n));
		return ret;
	}
}
