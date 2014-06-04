package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;
import org.rnd.jmagic.sanitized.SanitizedEvent;

import java.util.Map;
import java.util.Set;

public final class Show extends EventType
{	public static final EventType INSTANCE = new Show();

	 private Show()
	{
		super("SHOW");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.OBJECT;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		Set<GameObject> objects = parameters.get(Parameter.OBJECT).getAll(GameObject.class);
		for(GameObject object: objects)
		{
			object = object.getPhysical();
			for(Player p: game.actualState.players)
			{
				object.setPhysicalVisibility(p, true);
				object.setActualVisibility(p, true);
				SanitizedEvent sanitized = new SanitizedEvent.Reveal(event, objects, p);
				p.alert(sanitized);
			}
		}

		event.setResult(Empty.set);
		return true;
	}
}