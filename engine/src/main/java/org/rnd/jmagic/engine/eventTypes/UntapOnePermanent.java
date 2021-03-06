package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.Map;

public final class UntapOnePermanent extends EventType
{	public static final EventType INSTANCE = new UntapOnePermanent();

	 private UntapOnePermanent()
	{
		super("UNTAP_ONE_PERMANENT");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.OBJECT;
	}

	@Override
	public boolean attempt(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		// 701.16b. ... Only tapped permanents can be untapped.
		if(!parameters.get(Parameter.OBJECT).getOne(GameObject.class).isTapped())
			return false;

		return true;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		boolean status = true;

		MagicSet result = new MagicSet();
		GameObject object = parameters.get(Parameter.OBJECT).getOne(GameObject.class);
		if(object.isTapped() && object.isPermanent())
		{
			object.getPhysical().setTapped(false);
			result.add(object);
		}
		else
			status = false;

		event.setResult(Identity.instance(result));
		return status;
	}
}