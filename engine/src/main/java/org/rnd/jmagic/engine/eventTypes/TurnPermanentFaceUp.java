package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class TurnPermanentFaceUp extends EventType
{	public static final EventType INSTANCE = new TurnPermanentFaceUp();

	 private TurnPermanentFaceUp()
	{
		super("TURN_PERMANENT_FACE_UP");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.OBJECT;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		Set<GameObject> result = new HashSet<GameObject>();
		GameObject actual = parameters.get(Parameter.OBJECT).getOne(GameObject.class);
		if(actual.isPermanent())
		{
			GameObject physical = actual.getPhysical();
			// 707.8. As a face-down permanent is turned face up, its
			// copiable values revert to its normal copiable values. Any
			// effects that have been applied to the face-down permanent
			// still apply to the face-up permanent. Any abilities relating
			// to the permanent entering the battlefield don't trigger and
			// don't have any effect, because the permanent has already
			// entered the battlefield.
			physical.faceDownValues = null;
			result.add(actual);
		}

		event.setResult(Identity.instance(result));
		return true;
	}
}