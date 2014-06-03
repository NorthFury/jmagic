package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

public class ManaAddedBy extends SetGenerator
{
	public static ManaAddedBy instance(SetGenerator what)
	{
		return new ManaAddedBy(what);
	}

	private SetGenerator what;

	private ManaAddedBy(SetGenerator what)
	{
		this.what = what;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();
		for(NonStaticAbility ability: this.what.evaluate(state, thisObject).getAll(NonStaticAbility.class))
		{
			ManaPool manaAdded = ability.getManaAdded();
			if(manaAdded != null)
				ret.addAll(manaAdded);
		}
		return ret;
	}

}
