package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.abilities.keywords.Haste;
import org.rnd.jmagic.abilities.keywords.Vigilance;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Name("Aurelia, the Warleader")
@SuperTypes({SuperType.LEGENDARY})
@Types({Type.CREATURE})
@SubTypes({SubType.ANGEL})
@ManaCost("2RRWW")
@Printings({@Printings.Printed(ex = Expansion.GATECRASH, r = Rarity.MYTHIC)})
@ColorIdentity({Color.WHITE, Color.RED})
public final class AureliatheWarleader extends Card
{
	public static final class AttackTracker extends Tracker<Map<Integer, Integer>>
	{
		// keys are object IDs, values are number of times that object has
		// attacked
		private HashMap<Integer, Integer> value = new HashMap<Integer, Integer>();
		private Map<Integer, Integer> unmodifiable = Collections.unmodifiableMap(this.value);

		@Override
		protected AttackTracker clone()
		{
			AttackTracker ret = (AttackTracker)super.clone();
			ret.value = new HashMap<Integer, Integer>(this.value);
			ret.unmodifiable = Collections.unmodifiableMap(ret.value);
			return ret;
		}

		@Override
		protected Map<Integer, Integer> getValueInternal()
		{
			return this.unmodifiable;
		}

		@Override
		protected void reset()
		{
			this.value.clear();
		}

		@Override
		protected boolean match(GameState state, Event event)
		{
			return event.type == EventType.DECLARE_ONE_ATTACKER;
		}

		@Override
		protected void update(GameState state, Event event)
		{
			GameObject attacker = event.parametersNow.get(EventType.Parameter.OBJECT).evaluate(state, null).getOne(GameObject.class);
			if(this.value.containsKey(attacker.ID))
				this.value.put(attacker.ID, this.value.get(attacker.ID) + 1);
			else
				this.value.put(attacker.ID, 1);
		}
	}

	/**
	 * evaluates to the number of times the given object has attacked this turn
	 */
	public static final class NumAttacks extends SetGenerator
	{
		private SetGenerator who;

		private NumAttacks(SetGenerator who)
		{
			this.who = who;
		}

		public static NumAttacks instance(SetGenerator who)
		{
			return new NumAttacks(who);
		}

		@Override
		public MagicSet evaluate(GameState state, Identified thisObject)
		{
			Map<Integer, Integer> trackerValue = state.getTracker(AttackTracker.class).getValue(state);
			GameObject attacker = this.who.evaluate(state, thisObject).getOne(GameObject.class);
			return new MagicSet(trackerValue.get(attacker.ID));
		}
	}

	public static final class AureliatheWarleaderAbility1 extends EventTriggeredAbility
	{
		public AureliatheWarleaderAbility1(GameState state)
		{
			super(state, "Whenever Aurelia, the Warleader attacks for the first time each turn, untap all creatures you control. After this phase, there is an additional combat phase.");
			this.addPattern(whenThisAttacks());

			state.ensureTracker(new AttackTracker());
			SetGenerator thisHasAttackedOnce = Intersect.instance(numberGenerator(1), NumAttacks.instance(ABILITY_SOURCE_OF_THIS));
			this.interveningIf = thisHasAttackedOnce;

			this.addEffect(untap(CREATURES_YOU_CONTROL, "Untap all creatures you control."));

			List<Phase.PhaseType> combatPhase = new LinkedList<Phase.PhaseType>();
			combatPhase.add(Phase.PhaseType.COMBAT);

			EventFactory moreCombat = new EventFactory(EventType.TAKE_EXTRA_PHASE, "After this phase, there is an additional combat phase.");
			moreCombat.parameters.put(EventType.Parameter.CAUSE, This.instance());
			moreCombat.parameters.put(EventType.Parameter.TARGET, CurrentPhase.instance());
			moreCombat.parameters.put(EventType.Parameter.PHASE, Identity.instance((Object)combatPhase));
			this.addEffect(moreCombat);
		}
	}

	public AureliatheWarleader(GameState state)
	{
		super(state);

		this.setPower(3);
		this.setToughness(4);

		// Flying, vigilance, haste
		this.addAbility(new Flying(state));
		this.addAbility(new Vigilance(state));
		this.addAbility(new Haste(state));

		// Whenever Aurelia, the Warleader attacks for the first time each turn,
		// untap all creatures you control. After this phase, there is an
		// additional combat phase.
		this.addAbility(new AureliatheWarleaderAbility1(state));
	}
}
