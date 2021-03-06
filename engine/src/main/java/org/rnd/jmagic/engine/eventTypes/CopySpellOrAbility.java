package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class CopySpellOrAbility extends EventType
{	public static final EventType INSTANCE = new CopySpellOrAbility();

	 private CopySpellOrAbility()
	{
		super("COPY_SPELL_OR_ABILITY");
	}

	@Override
	public Parameter affects()
	{
		return null;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		Player controller = null;
		if(parameters.containsKey(Parameter.PLAYER))
			controller = parameters.get(Parameter.PLAYER).getOne(Player.class);

		MagicSet controllerSet = null;
		if(controller != null)
			controllerSet = new MagicSet(controller);

		MagicSet result = new MagicSet();

		int number = (parameters.containsKey(Parameter.NUMBER) ? Sum.get(parameters.get(Parameter.NUMBER)) : 1);

		Map<GameObject, GameObject> copies = new HashMap<GameObject, GameObject>();

		for(GameObject object: parameters.get(Parameter.OBJECT).getAll(GameObject.class))
		{
			for(int i = 0; i < number; i++)
			{
				GameObject copy = null;
				if(object.isSpell() || object.isCard())
					copy = new SpellCopy(game.physicalState, object.getName());
				else if(object.isActivatedAbility() || object.isTriggeredAbility())
				{
					// 706.9b A copy of an ability has the same source as
					// the original ability. If the ability refers to its
					// source by name, the copy refers to that same object
					// and not to any other object with the same name. The
					// copy is considered to be the same ability by effects
					// that count how many times that ability has resolved
					// during the turn.
					NonStaticAbility ability = (NonStaticAbility)object;
					NonStaticAbility create = (NonStaticAbility)ability.create(game);
					create.sourceID = ability.sourceID;
					copy = create;
				}
				else
					throw new UnsupportedOperationException("Trying to cast something that isn't a spell, activated ability, or triggered ability");

				if(controller == null)
					copy.ownerID = object.ownerID;
				else
					copy.ownerID = controller.ID;
				game.physicalState.exileZone().addToTop(copy);
				copies.put(copy, object);

				// keep Identity happy
				game.actualState.put(copy);
			}
		}

		if(!copies.isEmpty())
		{
			for(Map.Entry<GameObject, GameObject> entry: copies.entrySet())
			{
				Zone toZone = entry.getValue().getZone();

				Map<Parameter, MagicSet> moveParameters = new HashMap<Parameter, MagicSet>();
				moveParameters.put(Parameter.CAUSE, parameters.get(Parameter.CAUSE));
				if(controllerSet != null)
					moveParameters.put(Parameter.CONTROLLER, controllerSet);
				moveParameters.put(Parameter.OBJECT, new MagicSet(entry.getKey()));
				moveParameters.put(Parameter.SOURCE, new MagicSet(entry.getValue()));
				moveParameters.put(Parameter.TO, new MagicSet(toZone));

				Event movedCopies = createEvent(game, "Put " + entry.getKey() + " onto " + toZone + ".", EventType.PUT_INTO_ZONE_AS_A_COPY_OF, moveParameters);
				movedCopies.perform(event, true);

				if(result == null)
					result = movedCopies.getResult();
				else
					result.addAll(movedCopies.getResult());
			}

			result = NewObjectOf.instance(Identity.instance(result)).evaluate(game, null);

			if(!parameters.containsKey(Parameter.TARGET))
			{
				// Refresh the state to apply all the copy effects
				game.refreshActualState();

				for(GameObject copy: result.getAll(GameObject.class))
				{
					Set<Integer> targets = new HashSet<Integer>();
					for(Mode m: copy.getSelectedModes())
						for(Target possibleTarget: m.targets)
							for(Target chosenTarget: copy.getChosenTargets().get(possibleTarget))
								targets.add(chosenTarget.targetID);

					if(!targets.isEmpty())
					{
						EventFactory changeTargetFactory = new EventFactory(EventType.CHANGE_TARGETS, ("Choose new targets for " + copy));
						changeTargetFactory.parameters.put(Parameter.OBJECT, Identity.instance(copy));
						changeTargetFactory.parameters.put(Parameter.PLAYER, Identity.instance(controller));

						EventFactory mayFactory = new EventFactory(EventType.PLAYER_MAY, "You may choose new targets for " + copy);
						mayFactory.parameters.put(Parameter.PLAYER, Identity.instance(controller));
						mayFactory.parameters.put(Parameter.EVENT, Identity.instance(changeTargetFactory));

						EventFactory becomesTargetFactory = new EventFactory(EventType.BECOMES_TARGET, "Targets remain the same.");
						becomesTargetFactory.parameters.put(Parameter.OBJECT, Identity.instance(copy));
						becomesTargetFactory.parameters.put(Parameter.TARGET, IdentifiedWithID.instance(targets));

						Map<Parameter, MagicSet> targetParameters = new HashMap<Parameter, MagicSet>();
						targetParameters.put(Parameter.IF, new MagicSet(mayFactory));
						targetParameters.put(Parameter.ELSE, new MagicSet(becomesTargetFactory));
						Event mayEvent = createEvent(game, "You may choose new targets for " + copy, EventType.IF_EVENT_THEN_ELSE, targetParameters);

						mayEvent.perform(event, false);
					}
				}
			}
		}

		event.setResult(result);

		return true;
	}
}