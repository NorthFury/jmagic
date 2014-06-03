package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

public final class TapOnePermanent extends EventType
{	public static final EventType INSTANCE = new TapOnePermanent();

	 private TapOnePermanent()
	{
		super("TAP_ONE_PERMANENT");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.OBJECT;
	}

	@Override
	public boolean attempt(Game game, Event event, java.util.Map<Parameter, MagicSet> parameters)
	{
		// 701.16a. ... Only untapped permanents can be tapped.
		if(parameters.get(Parameter.OBJECT).getOne(GameObject.class).isTapped())
			return false;

		return true;
	}

	@Override
	public boolean perform(Game game, Event event, java.util.Map<Parameter, MagicSet> parameters)
	{
		boolean status = true;

		MagicSet result = new MagicSet();
		GameObject object = parameters.get(Parameter.OBJECT).getOne(GameObject.class);
		if(!object.isTapped() && ((-1 == object.zoneID) || object.isPermanent()))
		{
			object.getPhysical().setTapped(true);
			result.add(object);
		}
		else
			status = false;

		event.setResult(Identity.instance(result));
		return status;
	}
}