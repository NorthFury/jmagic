package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Evaluates to each GameObject which has the any of the given Types
 */
public class HasType extends SetGenerator
{
	private static final Map<Type, HasType> _instances = new HashMap<Type, HasType>();

	public static MagicSet get(GameState state, Collection<Type> types)
	{
		MagicSet ret = new MagicSet();
		for(GameObject item: state.getAllObjects())
			for(Type type: types)
				if(item.getTypes().contains(type))
				{
					ret.add(item);
					break;
				}
		return ret;
	}

	public static HasType instance(Type what)
	{
		if(!_instances.containsKey(what))
			_instances.put(what, new HasType(Identity.instance(what)));
		return _instances.get(what);
	}

	public static HasType instance(Type... what)
	{
		return new HasType(Identity.instance((Object[])what));
	}

	public static SetGenerator instance(Collection<Type> what)
	{
		return new HasType(Identity.instance(what));
	}

	public static HasType instance(SetGenerator what)
	{
		return new HasType(what);
	}

	private final SetGenerator type;

	private HasType(SetGenerator type)
	{
		this.type = type;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		return get(state, this.type.evaluate(state, thisObject).getAll(Type.class));
	}
}
