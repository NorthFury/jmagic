package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Evaluates to a Set of Sets. Each Set contains objects owned by a single
 * player.
 */
public class SplitOnOwner extends SetGenerator
{
	public static SplitOnOwner instance(SetGenerator what)
	{
		return new SplitOnOwner(what);
	}

	private SetGenerator what;

	private SplitOnOwner(SetGenerator what)
	{
		this.what = what;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		Map<Integer, MagicSet> whoControlsWhat = new HashMap<Integer, MagicSet>();
		for(Player p: state.players)
			whoControlsWhat.put(p.ID, new MagicSet());
		for(GameObject object: this.what.evaluate(state, thisObject).getAll(GameObject.class))
			whoControlsWhat.get(object.ownerID).add(object);
		return new MagicSet(whoControlsWhat.values());
	}
}
