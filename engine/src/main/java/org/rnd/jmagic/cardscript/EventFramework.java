package org.rnd.jmagic.cardscript;

import org.rnd.jmagic.engine.*;

import java.util.HashMap;
import java.util.Map;

public class EventFramework
{
	public EventType type = null;
	public Map<EventType.Parameter, String> parameters = new HashMap<EventType.Parameter, String>();
	public EventType.Parameter actorParameter = EventType.Parameter.CAUSE;
	public EventType.Parameter acteeParameter = EventType.Parameter.OBJECT;

	public void setActor(String actor)
	{
		this.parameters.put(this.actorParameter, actor);
	}

	public void setActee(String actee)
	{
		this.parameters.put(this.acteeParameter, actee);
	}
}
