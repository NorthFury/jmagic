package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Evaluates to the player(s) with the given id(s)
 */
public class PlayerByID extends SetGenerator
{
	public static PlayerByID instance(int what)
	{
		return new PlayerByID(what);
	}

	public static PlayerByID instance(Collection<Integer> what)
	{
		return new PlayerByID(what);
	}

	private final Collection<Integer> IDs;

	private PlayerByID(int ID)
	{
		this.IDs = new LinkedList<Integer>();
		this.IDs.add(ID);
	}

	private PlayerByID(Collection<Integer> IDs)
	{
		this.IDs = new LinkedList<Integer>(IDs);
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();
		for(Integer n: this.IDs)
			ret.add(state.get(n));
		return ret;
	}
}
