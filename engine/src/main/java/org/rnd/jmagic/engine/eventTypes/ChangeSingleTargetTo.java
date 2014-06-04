package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ChangeSingleTargetTo extends EventType
{	public static final EventType INSTANCE = new ChangeSingleTargetTo();

	 private ChangeSingleTargetTo()
	{
		super("CHANGE_SINGLE_TARGET_TO");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.OBJECT;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		GameObject object = parameters.get(Parameter.OBJECT).getOne(GameObject.class);
		Identified newTarget = parameters.get(Parameter.TARGET).getOne(Identified.class);
		Set<Target> canBeChanged = new HashSet<Target>();

		Map<Target, Boolean> wasLegal = new HashMap<Target, Boolean>();

		{
			Set<Integer> restricted = new HashSet<Integer>();
			for(Mode checkMode: object.getSelectedModes())
			{
				for(Target checkBaseTarget: checkMode.targets)
				{
					Set<Integer> thisBaseTarget = new HashSet<Integer>();
					for(Target checkTarget: object.getChosenTargets().get(checkBaseTarget))
					{
						MagicSet legalCheck = checkTarget.legalChoicesNow(game, object);
						boolean legal = !(restricted.contains(checkTarget.targetID) || !legalCheck.contains(game.actualState.get(checkTarget.targetID)));
						wasLegal.put(checkTarget, legal);
						if(checkTarget.restrictFromLaterTargets)
							restricted.add(checkTarget.targetID);
						thisBaseTarget.add(checkTarget.targetID);
					}
				}
			}
		}

		for(Mode mode: object.getSelectedModes())
		{
			for(Target baseTarget: mode.targets)
			{
				for(Target target: object.getChosenTargets().get(baseTarget))
				{
					MagicSet targets = target.legalChoicesNow(game, object);
					if(targets.contains(newTarget))
					{
						// The target won't "change" if its already set to
						// this target.
						if(target.targetID == newTarget.ID)
							continue;

						int oldValue = target.targetID;
						target.targetID = newTarget.ID;

						boolean illegal = false;
						Set<Integer> restricted = new HashSet<Integer>();
						legalityCheck: for(Mode checkMode: object.getSelectedModes())
						{
							for(Target checkBaseTarget: checkMode.targets)
							{
								Set<Integer> thisBaseTarget = new HashSet<Integer>();
								for(Target checkTarget: object.getChosenTargets().get(checkBaseTarget))
								{
									MagicSet legalCheck = checkTarget.legalChoicesNow(game, object);
									boolean targetWasLegal = wasLegal.get(checkTarget);
									if(targetWasLegal && (thisBaseTarget.contains(checkTarget.targetID) || restricted.contains(checkTarget.targetID) || !legalCheck.contains(game.actualState.get(checkTarget.targetID))))
									{
										illegal = true;
										break legalityCheck;
									}
									if(checkTarget.restrictFromLaterTargets)
										restricted.add(checkTarget.targetID);
									thisBaseTarget.add(checkTarget.targetID);
								}
							}
						}

						target.targetID = oldValue;

						if(!illegal)
						{
							canBeChanged.add(target);
						}
					}
				}
			}
		}

		Player player = parameters.get(Parameter.PLAYER).getOne(Player.class);
		List<Target> choice = player.sanitizeAndChoose(game.actualState, 1, canBeChanged, PlayerInterface.ChoiceType.SINGLE_TARGET, PlayerInterface.ChooseReason.TARGET);

		if(!choice.isEmpty())
		{
			event.setResult(Identity.instance(object));
			Target chosenTarget = choice.get(0);
			chosenTarget.targetID = newTarget.ID;

			// Also set the target on the physical object
			setPhysicalTarget: for(Map.Entry<Target, List<Target>> entry: object.getChosenTargets().entrySet())
			{
				for(int i = 0; i < entry.getValue().size(); ++i)
				{
					if(entry.getValue().get(i) == chosenTarget)
					{
						GameObject physical = object.getPhysical();
						physical.getChosenTargets().get(entry.getKey()).get(i).targetID = newTarget.ID;
						break setPhysicalTarget;
					}
				}
			}
		}
		else
		{
			event.setResult(Identity.instance());
		}

		return choice.size() == 1;
	}

}