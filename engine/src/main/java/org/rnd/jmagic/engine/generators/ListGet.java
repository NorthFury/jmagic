package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

import java.util.List;

public class ListGet extends SetGenerator
{
	public static ListGet instance(SetGenerator list, int index)
	{
		return new ListGet(list, index);
	}

	private SetGenerator list;
	private int index;

	private ListGet(SetGenerator list, int index)
	{
		this.list = list;
		this.index = index;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();
		for(List<?> list: this.list.evaluate(state, thisObject).getAll(List.class))
			if(list.size() > this.index)
				ret.add(list.get(this.index));
		return ret;
	}
}
