package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.Map;

public final class Unattach extends EventType
{	public static final EventType INSTANCE = new Unattach();

	 private Unattach()
	{
		super("UNATTACH");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.OBJECT;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		MagicSet ret = new MagicSet();
		for(GameObject o: parameters.get(Parameter.OBJECT).getAll(GameObject.class))
		{
			AttachableTo previouslyAttachedTo = (AttachableTo)game.actualState.get(o.getAttachedTo());
			previouslyAttachedTo.getPhysical().removeAttachment(o.ID);

			ret.add(previouslyAttachedTo);
			o.getPhysical().setAttachedTo(-1);
		}
		event.setResult(Identity.instance(ret));
		return true;
	}
}