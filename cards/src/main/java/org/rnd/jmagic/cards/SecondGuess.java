package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Storm;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.List;

@Name("Second Guess")
@Types({Type.INSTANT})
@ManaCost("1U")
@Printings({@Printings.Printed(ex = Expansion.AVACYN_RESTORED, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.BLUE})
public final class SecondGuess extends Card
{
	public static class NthSpellCastThisTurn extends SetGenerator
	{
		public static SetGenerator instance(int n)
		{
			return new NthSpellCastThisTurn(n);
		}

		private int n;

		private NthSpellCastThisTurn(int n)
		{
			this.n = n;
		}

		@Override
		public MagicSet evaluate(GameState state, Identified thisObject)
		{
			List<Integer> spells = state.getTracker(Storm.StormTracker.class).getValue(state);
			if(spells.size() < this.n)
				return Empty.set;
			return new MagicSet(state.get(spells.get(this.n - 1)));
		}
	}

	public SecondGuess(GameState state)
	{
		super(state);

		// Counter target spell that's the second spell cast this turn.
		state.ensureTracker(new Storm.StormTracker());
		SetGenerator target = targetedBy(this.addTarget(NthSpellCastThisTurn.instance(2), "target spell that's the second spell cast this turn"));
		this.addEffect(counter(target, "Counter target spell that's the second spell cast this turn."));
	}
}
