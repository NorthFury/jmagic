package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.FirstStrike;
import org.rnd.jmagic.abilities.keywords.Vigilance;
import org.rnd.jmagic.engine.*;

@Name("Truefire Paladin")
@Types({Type.CREATURE})
@SubTypes({SubType.HUMAN, SubType.KNIGHT})
@ManaCost("RW")
@Printings({@Printings.Printed(ex = Expansion.GATECRASH, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.WHITE, Color.RED})
public final class TruefirePaladin extends Card
{
	public static final class TruefirePaladinAbility1 extends ActivatedAbility
	{
		public TruefirePaladinAbility1(GameState state)
		{
			super(state, "(R)(W): Truefire Paladin gets +2/+0 until end of turn.");
			this.setManaCost(new ManaPool("(R)(W)"));
			this.addEffect(ptChangeUntilEndOfTurn(ABILITY_SOURCE_OF_THIS, +2, +0, "Truefire Paladin gets +2/+0 until end of turn."));
		}
	}

	public static final class TruefirePaladinAbility2 extends ActivatedAbility
	{
		public TruefirePaladinAbility2(GameState state)
		{
			super(state, "(R)(W): Truefire Paladin gains first strike until end of turn.");
			this.setManaCost(new ManaPool("(R)(W)"));
			this.addEffect(addAbilityUntilEndOfTurn(ABILITY_SOURCE_OF_THIS, FirstStrike.class, "Truefire Paladin gains first strike until end of turn."));
		}
	}

	public TruefirePaladin(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(2);

		// Vigilance
		this.addAbility(new Vigilance(state));

		// (R)(W): Truefire Paladin gets +2/+0 until end of turn.
		this.addAbility(new TruefirePaladinAbility1(state));

		// (R)(W): Truefire Paladin gains first strike until end of turn.
		this.addAbility(new TruefirePaladinAbility2(state));
	}
}
