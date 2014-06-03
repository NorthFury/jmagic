package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

/**
 * Evaluates to the turn in which the given {@link FloatingContinuousEffect} was
 * created.
 */
public class TurnFloatingEffectWasCreated extends SetGenerator
{
	public static TurnFloatingEffectWasCreated instance(SetGenerator what)
	{
		return new TurnFloatingEffectWasCreated(what);
	}

	private final SetGenerator effects;

	private TurnFloatingEffectWasCreated(SetGenerator abilities)
	{
		this.effects = abilities;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();
		MagicSet effects = this.effects.evaluate(state, thisObject);
		for(FloatingContinuousEffect effect: effects.getAll(FloatingContinuousEffect.class))
			ret.add(effect.turnCreated);
		return ret;
	}
}
