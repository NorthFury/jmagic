package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

/**
 * Evaluates to the current phase
 */
public class CurrentPhase extends SetGenerator
{
	private static final CurrentPhase _instance = new CurrentPhase();

	public static CurrentPhase instance()
	{
		return _instance;
	}

	private CurrentPhase()
	{
		// Intentionally left ineffectual
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		if(state.currentPhase() != null)
			return new MagicSet(state.currentPhase());
		return Empty.set;
	}
}
