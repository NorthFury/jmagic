package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.abilities.keywords.Haste;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Name("Hellkite Charger")
@Types({Type.CREATURE})
@SubTypes({SubType.DRAGON})
@ManaCost("4RR")
@Printings({@Printings.Printed(ex = Expansion.ZENDIKAR, r = Rarity.RARE)})
@ColorIdentity({Color.RED})
public final class HellkiteCharger extends Card
{
	/**
	 * @eparam CAUSE: hellkite charger's trigger
	 * @eparam OBJECT: what to untap (all attacking creatures)
	 */
	public static final EventType HELLKITE_CHARGER_EVENT = new EventType("HELLKITE_CHARGER_EVENT")
	{
		@Override
		public Parameter affects()
		{
			return null;
		}

		@Override
		public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
		{
			MagicSet cause = parameters.get(Parameter.CAUSE);
			MagicSet untap = parameters.get(Parameter.OBJECT);

			Map<Parameter, MagicSet> untapParameters = new HashMap<Parameter, MagicSet>();
			untapParameters.put(Parameter.CAUSE, cause);
			untapParameters.put(Parameter.OBJECT, untap);
			createEvent(game, "Untap all attacking creatures", EventType.UNTAP_PERMANENTS, untapParameters).perform(event, true);

			List<Phase.PhaseType> combatPhaseList = new LinkedList<Phase.PhaseType>();
			combatPhaseList.add(Phase.PhaseType.COMBAT);
			MagicSet combatPhaseSet = new MagicSet();
			combatPhaseSet.add(combatPhaseList);

			Map<Parameter, MagicSet> combatParameters = new HashMap<Parameter, MagicSet>();
			combatParameters.put(Parameter.CAUSE, cause);
			combatParameters.put(Parameter.TARGET, new MagicSet(game.actualState.currentPhase()));
			combatParameters.put(Parameter.PHASE, combatPhaseSet);
			createEvent(game, "There is an additional combat phase", EventType.TAKE_EXTRA_PHASE, combatParameters).perform(event, true);

			event.setResult(Empty.set);
			return true;
		}
	};

	public static final class MayPayForAnotherCombatPhase extends EventTriggeredAbility
	{
		public MayPayForAnotherCombatPhase(GameState state)
		{
			super(state, "Whenever Hellkite Charger attacks, you may pay (5)(R)(R). If you do, untap all attacking creatures and after this phase, there is an additional combat phase.");
			this.addPattern(whenThisAttacks());

			EventFactory mayPay = new EventFactory(EventType.PLAYER_MAY_PAY_MANA, "You may pay (5)(R)(R).");
			mayPay.parameters.put(EventType.Parameter.CAUSE, This.instance());
			mayPay.parameters.put(EventType.Parameter.PLAYER, You.instance());
			mayPay.parameters.put(EventType.Parameter.COST, Identity.instance(new ManaPool("5RR")));

			EventFactory moreFighting = new EventFactory(HELLKITE_CHARGER_EVENT, "Untap all attacking creatures and after this phase, there is an additional combat phase");
			moreFighting.parameters.put(EventType.Parameter.CAUSE, This.instance());
			moreFighting.parameters.put(EventType.Parameter.OBJECT, Attacking.instance());

			EventFactory effect = new EventFactory(EventType.IF_EVENT_THEN_ELSE, "You may pay (5)(R)(R). If you do, untap all attacking creatures and after this phase, there is an additional combat phase.");
			effect.parameters.put(EventType.Parameter.IF, Identity.instance(mayPay));
			effect.parameters.put(EventType.Parameter.THEN, Identity.instance(moreFighting));
			this.addEffect(effect);
		}
	}

	public HellkiteCharger(GameState state)
	{
		super(state);

		this.setPower(5);
		this.setToughness(5);

		// Flying, haste
		this.addAbility(new Flying(state));
		this.addAbility(new Haste(state));

		// Whenever Hellkite Charger attacks, you may pay (5)(R)(R). If you do,
		// untap all attacking creatures and after this phase, there is an
		// additional combat phase.
		this.addAbility(new MayPayForAnotherCombatPhase(state));
	}
}
