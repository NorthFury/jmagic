package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

public class SelectedModesOf extends SetGenerator
{
	public static SelectedModesOf instance(SetGenerator what)
	{
		return new SelectedModesOf(what);
	}

	private SetGenerator what;

	private SelectedModesOf(SetGenerator what)
	{
		this.what = what;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();

		for(GameObject object: this.what.evaluate(state, thisObject).getAll(GameObject.class))
			ret.addAll(object.getSelectedModes());

		return ret;
	}

}
