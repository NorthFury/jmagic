package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.engine.*;

@Name("Aven Flock")
@Types({Type.CREATURE})
@SubTypes({SubType.SOLDIER, SubType.BIRD})
@ManaCost("4W")
@Printings({@Printings.Printed(ex = Expansion.NINTH_EDITION, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.EIGHTH_EDITION, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.ODYSSEY, r = Rarity.COMMON)})
@ColorIdentity({Color.WHITE})
public final class AvenFlock extends Card
{
	public static final class AvenFlockAbility1 extends ActivatedAbility
	{
		public AvenFlockAbility1(GameState state)
		{
			super(state, "(W): Aven Flock gets +0/+1 until end of turn.");
			this.setManaCost(new ManaPool("(W)"));

			this.addEffect(ptChangeUntilEndOfTurn(ABILITY_SOURCE_OF_THIS, (+0), (+1), "Aven Flock gets +0/+1 until end of turn."));
		}
	}

	public AvenFlock(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(3);

		// Flying (This creature can't be blocked except by creatures with
		// flying or reach.)
		this.addAbility(new Flying(state));

		// (W): Aven Flock gets +0/+1 until end of turn.
		this.addAbility(new AvenFlockAbility1(state));
	}
}
