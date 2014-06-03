package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

/**
 * Evaluates to the stack
 */
public class Stack extends org.rnd.jmagic.engine.SetGenerator
{
	private static final Stack _instance = new Stack();

	public static Stack instance()
	{
		return _instance;
	}

	private Stack()
	{
		// Intentionally non-functional
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		return new MagicSet(state.stack());
	}
}
