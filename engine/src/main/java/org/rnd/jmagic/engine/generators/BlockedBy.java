package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

/**
 * Evaluates to each object being blocked by the specified blocker(s)
 */
public class BlockedBy extends SetGenerator
{
	public static BlockedBy instance(SetGenerator what)
	{
		return new BlockedBy(what);
	}

	private final SetGenerator what;

	private BlockedBy(SetGenerator what)
	{
		this.what = what;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet evaluateWhat = this.what.evaluate(state, thisObject);
		MagicSet ret = new MagicSet();

		for(GameObject blocker: evaluateWhat.getAll(GameObject.class))
			for(int blockerID: blocker.getBlockingIDs())
				if(state.containsIdentified(blockerID))
					ret.add(state.get(blockerID));

		return ret;
	}
}
