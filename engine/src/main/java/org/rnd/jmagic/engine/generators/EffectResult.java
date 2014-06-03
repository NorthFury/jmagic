package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

public class EffectResult extends SetGenerator
{
	public static EffectResult instance(EventFactory factory)
	{
		factory.preserveCreatedEvents();
		return new EffectResult(Identity.instance(factory));
	}

	private final SetGenerator factories;

	private EffectResult(SetGenerator factories)
	{
		this.factories = factories;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();
		if(thisObject.isGameObject())
		{
			GameObject o = (GameObject)thisObject;
			for(EventFactory f: this.factories.evaluate(state, thisObject).getAll(EventFactory.class))
			{
				Event effectGenerated = o.getEffectGenerated(state, f);
				if(null != effectGenerated)
					ret.addAll(effectGenerated.getResult(state));
			}
		}
		return ret;
	}
}
