package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

import java.util.Collection;
import java.util.Set;

/**
 * Evaluates to each GameObject which has the at least one of the given SubTypes
 */
public class HasSubType extends SetGenerator
{
	public static HasSubType instance(SubType what)
	{
		return new HasSubType(Identity.instance(what));
	}

	public static HasSubType instance(SubType... what)
	{
		return new HasSubType(Identity.instance((Object[])what));
	}

	public static HasSubType instance(SetGenerator what)
	{
		return new HasSubType(what);
	}

	public static HasSubType instance(Collection<SubType> what)
	{
		return new HasSubType(Identity.instance(what));
	}

	private final SetGenerator type;

	private HasSubType(SetGenerator type)
	{
		this.type = type;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();
		Set<SubType> types = this.type.evaluate(state, thisObject).getAll(SubType.class);
		for(GameObject item: state.getAllObjects())
			for(SubType type: types)
				if(item.getSubTypes().contains(type))
				{
					ret.add(item);
					break;
				}
		return ret;
	}
}
