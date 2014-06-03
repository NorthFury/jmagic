package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

public class EventSource extends SetGenerator
{
	public static EventSource instance(SetGenerator what)
	{
		return new EventSource(what);
	}

	private SetGenerator what;

	private EventSource(SetGenerator what)
	{
		this.what = what;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();
		for(Event event: this.what.evaluate(state, thisObject).getAll(Event.class))
		{
			GameObject source = event.getSource();
			if(null != source)
				ret.add(source);
		}
		return ret;
	}

}
