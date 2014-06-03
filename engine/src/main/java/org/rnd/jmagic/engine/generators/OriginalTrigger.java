package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

public class OriginalTrigger extends SetGenerator
{
	public static OriginalTrigger instance(SetGenerator what)
	{
		return new OriginalTrigger(what);
	}

	private SetGenerator what;

	private OriginalTrigger(SetGenerator what)
	{
		this.what = what;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();
		for(TriggeredAbility ability: this.what.evaluate(state, thisObject).getAll(TriggeredAbility.class))
		{
			NonStaticAbility original = ability.getPrintedVersion(state);
			if(original != null)
				ret.add(original);
		}
		return ret;
	}
}
