package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

public class EffectDamage extends SetGenerator
{
	public static EffectDamage instance(EventFactory factory)
	{
		factory.preserveCreatedEvents();
		return new EffectDamage(Identity.instance(factory));
	}

	private final SetGenerator factories;

	private EffectDamage(SetGenerator factories)
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
					ret.addAll(effectGenerated.getDamage());
			}
		}
		return ret;
	}
}
