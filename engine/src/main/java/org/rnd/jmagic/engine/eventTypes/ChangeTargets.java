package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ChangeTargets extends EventType
{	public static final EventType INSTANCE = new ChangeTargets();

	 private ChangeTargets()
	{
		super("CHANGE_TARGETS");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.OBJECT;
	}

	@Override
	public boolean attempt(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		if(parameters.get(Parameter.PLAYER).getOne(Player.class) == null)
			return false;

		for(GameObject object: parameters.get(Parameter.OBJECT).getAll(GameObject.class))
			if(!this.canBeChanged(game, object))
				return false;
		return true;
	}

	private boolean canBeChanged(Game game, GameObject originalObject)
	{
		for(Mode mode: originalObject.getSelectedModes())
		{
			List<Integer> ignoreThese = new LinkedList<Integer>();
			ignoreThese.add(originalObject.ID);

			for(Target possibleTarget: mode.targets)
			{
				if(!originalObject.getChosenTargets().containsKey(possibleTarget))
					continue;

				for(Target chosenTarget: originalObject.getChosenTargets().get(possibleTarget))
				{
					int previousTarget = chosenTarget.targetID;

					MagicSet legalTargetsNow = chosenTarget.legalChoicesNow(game, originalObject);
					Set<Target> targetSet = new HashSet<Target>();

					for(Identified targetObject: legalTargetsNow.getAll(Identified.class))
						if(!ignoreThese.contains(targetObject.ID) && previousTarget != targetObject.ID)
							targetSet.add(new Target(targetObject, chosenTarget));

					if(targetSet.size() == 0)
						return false;

					if(chosenTarget.restrictFromLaterTargets)
						ignoreThese.add(targetSet.iterator().next().targetID);
				}
			}
		}
		return true;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		Player chooser = parameters.get(Parameter.PLAYER).getOne(Player.class);

		MagicSet result = new MagicSet();
		boolean ret = true;

		for(GameObject object: parameters.get(Parameter.OBJECT).getAll(GameObject.class))
		{
			// if the object can be changed and is changed successfully, add
			// it to results. Otherwise, we're returning false.
			if(this.canBeChanged(game, object) && object.reselectTargets(chooser))
			{
				// Update the physical object, unless this is the physical
				// object.
				for(GameObject objectToUpdate: object.andPhysical())
				{
					if(objectToUpdate == object)
						continue;
					objectToUpdate.getChosenTargets().clear();
					objectToUpdate.getChosenTargets().putAll(object.getChosenTargets());
				}
				result.add(object);
			}
			else
				ret = false;
		}

		event.setResult(Identity.instance(result));

		return ret;
	}
}