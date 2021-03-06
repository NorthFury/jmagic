package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Haste;
import org.rnd.jmagic.engine.*;

@Name("Kamahl, Pit Fighter")
@SuperTypes({SuperType.LEGENDARY})
@Types({Type.CREATURE})
@SubTypes({SubType.HUMAN, SubType.BARBARIAN})
@ManaCost("4RR")
@Printings({@Printings.Printed(ex = Expansion.TENTH_EDITION, r = Rarity.RARE), @Printings.Printed(ex = Expansion.ODYSSEY, r = Rarity.RARE)})
@ColorIdentity({Color.RED})
public final class KamahlPitFighter extends Card
{
	public static final class TapForThreeDamage extends ActivatedAbility
	{
		public TapForThreeDamage(GameState state)
		{
			super(state, "(T): Kamahl, Pit Fighter deals 3 damage to target creature or player.");

			this.costsTap = true;

			Target target = this.addTarget(CREATURES_AND_PLAYERS, "target creature or player");

			this.addEffect(permanentDealDamage(3, targetedBy(target), "Kamahl, Pit Fighter deals 3 damage to target creature or player."));
		}
	}

	public KamahlPitFighter(GameState state)
	{
		super(state);

		this.setPower(6);
		this.setToughness(1);

		this.addAbility(new Haste(state));
		this.addAbility(new TapForThreeDamage(state));
	}
}
