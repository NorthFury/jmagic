package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

public class ActivatedAbilitiesOf extends SetGenerator
{
	public static ActivatedAbilitiesOf instance(SetGenerator what)
	{
		return new ActivatedAbilitiesOf(what);
	}

	private SetGenerator what;

	private ActivatedAbilitiesOf(SetGenerator what)
	{
		this.what = what;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();

		for(GameObject object: this.what.evaluate(state, thisObject).getAll(GameObject.class))
			for(NonStaticAbility ability: object.getNonStaticAbilities())
				if(ability instanceof ActivatedAbility)
					ret.add(ability);

		return ret;
	}

}
