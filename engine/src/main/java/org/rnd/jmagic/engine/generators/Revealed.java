package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

public class Revealed extends SetGenerator
{
	private static Revealed _instance = null;

	public static Revealed instance()
	{
		if(_instance == null)
			_instance = new Revealed();
		return _instance;
	}

	private Revealed()
	{
		// intentionally blank
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();

		int numPlayers = state.players.size();

		for(GameObject object: state.getAllObjects())
			if(object.getVisibleTo().size() == numPlayers)
				ret.add(object);

		return ret;
	}
}
