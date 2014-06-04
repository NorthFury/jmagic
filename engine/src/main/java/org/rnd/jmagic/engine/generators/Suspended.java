package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.abilities.keywords.Suspend;
import org.rnd.jmagic.engine.*;

public class Suspended extends SetGenerator
{
	private static SetGenerator _instance = null;

	public static SetGenerator instance()
	{
		if(_instance == null)
			_instance = Intersect.instance(InZone.instance(ExileZone.instance()), Intersect.instance(HasKeywordAbility.instance(Suspend.class), HasCounterOfType.instance(Counter.CounterType.TIME)));
		return _instance;
	}

	private Suspended()
	{
		// Private constructor for singleton design
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();

		for(GameObject object: state.exileZone().objects)
			if(object.isSuspended())
				ret.add(object);

		return ret;
	}
}
