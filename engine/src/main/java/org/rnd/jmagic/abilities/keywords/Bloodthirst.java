package org.rnd.jmagic.abilities.keywords;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.LinkedList;
import java.util.List;

public abstract class Bloodthirst extends Keyword
{
	protected final int N;

	public Bloodthirst(GameState state, int N)
	{
		super(state, "Bloodthirst " + N);
		this.N = N;
	}

	@Override
	protected List<StaticAbility> createStaticAbilities()
	{
		List<StaticAbility> ret = new LinkedList<StaticAbility>();
		ret.add(new BloodthirstAbility(this.state, this.N));
		return ret;
	}

	public static final class BloodthirstAbility extends StaticAbility
	{
		private final int N;

		public BloodthirstAbility(GameState state, int N)
		{
			super(state, "If an opponent was dealt damage this turn, this permanent comes into play with " + N + " +1/+1 counters on it.");
			this.N = N;

			ZoneChangeReplacementEffect bloodthirstCounters = new ZoneChangeReplacementEffect(this.game, "This permanent comes into play with " + N + " +1/+1 counters on it");
			bloodthirstCounters.addPattern(asThisEntersTheBattlefield());
			bloodthirstCounters.addEffect(putCounters(N, Counter.CounterType.PLUS_ONE_PLUS_ONE, NewObjectOf.instance(bloodthirstCounters.replacedByThis()), "This permanent comes into player with " + N + " +1/+1 counters on it."));
			this.addEffectPart(replacementEffectPart(bloodthirstCounters));

			// TODO : You.instance()?
			SetGenerator controller = ControllerOf.instance(This.instance());
			SetGenerator opponents = OpponentsOf.instance(controller);

			state.ensureTracker(new DamageDealtToThisTurn.Tracker());
			SetGenerator damageToOpponents = DamageDealtToThisTurn.instance(opponents);
			SetGenerator opponentTookDamage = Not.instance(Intersect.instance(numberGenerator(0), damageToOpponents));
			this.canApply = opponentTookDamage;
		}

		@Override
		public BloodthirstAbility create(Game game)
		{
			return new BloodthirstAbility(game.physicalState, this.N);
		}
	}

	public static final class Final extends Bloodthirst
	{
		public Final(GameState state, int number)
		{
			super(state, number);
		}

		@Override
		public Final create(Game game)
		{
			return new Final(game.physicalState, this.N);
		}
	}
}
