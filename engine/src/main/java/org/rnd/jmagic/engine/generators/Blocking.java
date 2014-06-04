package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Evaluates to each blocking object of the specified attacker(s), or just each
 * blocking object. It is safe to assume that this will only return creatures.
 */
public class Blocking extends SetGenerator
{
	private static final Blocking _instance = new Blocking();

	public static Blocking instance()
	{
		return _instance;
	}

	public static Blocking instance(SetGenerator what)
	{
		return new Blocking(what);
	}

	private final SetGenerator what;

	private Blocking()
	{
		this.what = null;
	}

	private Blocking(SetGenerator what)
	{
		this.what = what;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet allBlockers = Blocking.get(state);
		if(this.what == null)
			return allBlockers;

		Set<Integer> attackerIDs = new HashSet<Integer>();
		MagicSet evaluateWhat = this.what.evaluate(state, thisObject);
		for(GameObject o: evaluateWhat.getAll(GameObject.class))
			attackerIDs.add(o.ID);

		Set<GameObject> blockingTheseAttackers = allBlockers.getAll(GameObject.class);
		MagicSet ret = new MagicSet();

		// For each blocker, if it is blocking a specified attacker, add it to
		// the return
		for(GameObject blocker: blockingTheseAttackers)
			for(Integer attackerID: attackerIDs)
				if(blocker.getBlockingIDs().contains(attackerID))
				{
					ret.add(blocker);
					break;
				}

		return ret;
	}

	public static MagicSet get(GameState state)
	{
		MagicSet ret = new MagicSet();
		for(GameObject o: state.battlefield())
			if(!o.getBlockingIDs().isEmpty())
				ret.add(o);
		return ret;
	}
}
