package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.AllSliverCreaturesHave;
import org.rnd.jmagic.abilities.keywords.Absorb;
import org.rnd.jmagic.engine.*;

@Name("Lymph Sliver")
@Types({Type.CREATURE})
@SubTypes({SubType.SLIVER})
@ManaCost("4W")
@Printings({@Printings.Printed(ex = Expansion.FUTURE_SIGHT, r = Rarity.COMMON)})
@ColorIdentity({Color.WHITE})
public final class LymphSliver extends Card
{
	@Name("Absorb 1")
	public static final class Absorb1 extends Absorb
	{
		public Absorb1(GameState state)
		{
			super(state, 1);
		}
	}

	public LymphSliver(GameState state)
	{
		super(state);

		this.setPower(3);
		this.setToughness(3);

		this.addAbility(new AllSliverCreaturesHave(state, Absorb1.class, "All Sliver creatures have absorb 1."));
	}
}
