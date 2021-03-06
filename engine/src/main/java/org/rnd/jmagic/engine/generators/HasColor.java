package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Evaluates to each GameObject which has at least one of the given colors
 * 
 * If you want each GameObject that has all the given colors, instead use
 * Intersect(HasColor(...), HasColor(...)).
 */
public class HasColor extends SetGenerator
{
	public static HasColor instance(Color what)
	{
		return new HasColor(Identity.instance(what));
	}

	public static HasColor instance(Color... what)
	{
		return new HasColor(Identity.instance((Object[])what));
	}

	public static HasColor instance(SetGenerator what)
	{
		return new HasColor(what);
	}

	public static HasColor instance(Collection<Color> colors)
	{
		return new HasColor(colors);
	}

	private final SetGenerator colorGenerator;

	private HasColor(SetGenerator colors)
	{
		this.colorGenerator = colors;
	}

	private HasColor(Collection<Color> color)
	{
		MagicSet colors = new MagicSet();
		colors.addAll(color);
		this.colorGenerator = Identity.instance(colors);
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		// TODO : change this to use state.getAllObjects() instead.
		MagicSet ret = new MagicSet();
		Collection<Color> colors = new LinkedList<Color>();
		colors.addAll(this.colorGenerator.evaluate(state, thisObject).getAll(Color.class));
		for(GameObject item: state.getAll(GameObject.class))
			for(Color c: colors)
				if(null != item.getColors() && item.getColors().contains(c))
				{
					ret.add(item);
					break;
				}
		return ret;
	}
}
