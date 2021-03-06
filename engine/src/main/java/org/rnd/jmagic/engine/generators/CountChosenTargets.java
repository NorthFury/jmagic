package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

import java.util.Set;

public class CountChosenTargets extends SetGenerator
{
	public static CountChosenTargets instance(SetGenerator what, SetGenerator targets)
	{
		return new CountChosenTargets(what, targets);
	}

	private SetGenerator what;
	private SetGenerator targets;

	private CountChosenTargets(SetGenerator what, SetGenerator targets)
	{
		this.what = what;
		this.targets = targets;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		int i = 0;
		Set<Target> targets = this.targets.evaluate(state, thisObject).getAll(Target.class);
		for(GameObject o: this.what.evaluate(state, thisObject).getAll(GameObject.class))
			for(Target target: targets)
				if(o.getChosenTargets().containsKey(target))
					i += o.getChosenTargets().get(target).size();
		return new MagicSet(i);
	}

}
