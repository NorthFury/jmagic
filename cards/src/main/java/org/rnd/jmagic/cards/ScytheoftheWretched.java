package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.StaticPTChange;
import org.rnd.jmagic.abilities.keywords.Equip;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.Map;

@Name("Scythe of the Wretched")
@Types({Type.ARTIFACT})
@SubTypes({SubType.EQUIPMENT})
@ManaCost("2")
@Printings({@Printings.Printed(ex = Expansion.MIRRODIN, r = Rarity.RARE)})
@ColorIdentity({})
public final class ScytheoftheWretched extends Card
{
	/**
	 * @eparam CAUSE: Scythe of the Wretched's triggered ability
	 * @eparam SOURCE: Scythe of the Wretched
	 * @eparam CONTROLLER: controller of CAUSE (will control the creature)
	 * @eparam EVENT: ZoneChange that caused CAUSE to trigger
	 */
	public static final EventType SCYTHE_OF_THE_WRETCHED_EVENT = new EventType("SCYTHE_OF_THE_WRETCHED_EVENT")
	{
		@Override
		public Parameter affects()
		{
			return null;
		}

		@Override
		public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
		{
			event.setResult(Empty.set);

			MagicSet ability = parameters.get(Parameter.CAUSE);
			MagicSet controller = parameters.get(Parameter.CONTROLLER);

			ZoneChange triggerEvent = parameters.get(Parameter.EVENT).getOne(ZoneChange.class);
			MagicSet thatCard = new MagicSet(game.actualState.get(triggerEvent.newObjectID));

			Map<Parameter, MagicSet> moveParameters = new HashMap<Parameter, MagicSet>();
			moveParameters.put(Parameter.CAUSE, ability);
			moveParameters.put(Parameter.CONTROLLER, controller);
			moveParameters.put(Parameter.OBJECT, thatCard);
			Event putOntoBattlefield = createEvent(game, "Return that card to the battlefield under your control", EventType.PUT_ONTO_BATTLEFIELD, moveParameters);
			putOntoBattlefield.perform(event, true);
			MagicSet thatCreature = NewObjectOf.instance(putOntoBattlefield.getResultGenerator()).evaluate(game, null);

			MagicSet scythe = parameters.get(Parameter.SOURCE);

			Map<Parameter, MagicSet> attachParameters = new HashMap<Parameter, MagicSet>();
			attachParameters.put(Parameter.OBJECT, scythe);
			attachParameters.put(Parameter.TARGET, thatCreature);
			Event attach = createEvent(game, "Attach Scythe of the Wretched to that creature.", ATTACH, attachParameters);
			attach.perform(event, true);

			return true;
		}
	};

	public static final class ReturnCreatures extends EventTriggeredAbility
	{
		public ReturnCreatures(GameState state)
		{
			super(state, "Whenever a creature dealt damage by equipped creature this turn dies, return that card to the battlefield under your control. Attach Scythe of the Wretched to that creature.");

			state.ensureTracker(new DealtDamageByThisTurn.DealtDamageByTracker());
			SetGenerator thisCard = ABILITY_SOURCE_OF_THIS;
			SetGenerator victims = DealtDamageByThisTurn.instance(EquippedBy.instance(thisCard));

			this.addPattern(whenXDies(Intersect.instance(CreaturePermanents.instance(), victims)));

			EventFactory effect = new EventFactory(SCYTHE_OF_THE_WRETCHED_EVENT, "Return that card to the battlefield under your control. Attach Scythe of the Wretched to that creature.");
			effect.parameters.put(EventType.Parameter.CAUSE, This.instance());
			effect.parameters.put(EventType.Parameter.CONTROLLER, You.instance());
			effect.parameters.put(EventType.Parameter.SOURCE, thisCard);
			effect.parameters.put(EventType.Parameter.EVENT, TriggerZoneChange.instance(This.instance()));
			this.addEffect(effect);
		}
	}

	public ScytheoftheWretched(GameState state)
	{
		super(state);

		// Equipped creature gets +2/+2.
		SetGenerator equippedCreature = EquippedBy.instance(This.instance());
		this.addAbility(new StaticPTChange(state, equippedCreature, "Equipped creature", +2, +2, false));

		// Whenever a creature dealt damage by equipped creature this turn is
		// put into a graveyard, return that card to the battlefield under your
		// control. Attach Scythe of the Wretched to that creature.
		this.addAbility(new ReturnCreatures(state));

		// Equip (4)
		this.addAbility(new Equip(state, "(4)"));
	}
}
