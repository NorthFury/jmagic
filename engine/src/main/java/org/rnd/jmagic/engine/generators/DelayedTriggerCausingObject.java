package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

public class DelayedTriggerCausingObject extends SetGenerator
{
	private SetGenerator ofWhat;

	private DelayedTriggerCausingObject(SetGenerator ofWhat)
	{
		this.ofWhat = ofWhat;
	}

	public static SetGenerator instance(SetGenerator ofWhat)
	{
		return new DelayedTriggerCausingObject(ofWhat);
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();
		for(DelayedTrigger t: this.ofWhat.evaluate(state, thisObject).getAll(DelayedTrigger.class))
			ret.add(t.getCausingObject(state));
		return ret;
	}
}