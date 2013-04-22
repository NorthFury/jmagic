package org.rnd.jmagic.cards;

import org.rnd.jmagic.engine.*;

@Name("Silver Myr")
@Types({Type.CREATURE, Type.ARTIFACT})
@SubTypes({SubType.MYR})
@ManaCost("2")
@Printings({@Printings.Printed(ex = Expansion.SCARS_OF_MIRRODIN, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.MIRRODIN, r = Rarity.COMMON)})
@ColorIdentity({Color.BLUE})
public final class SilverMyr extends Card
{
	public SilverMyr(GameState state)
	{
		super(state);

		this.setPower(1);
		this.setToughness(1);

		// (T): Add (U) to your mana pool.
		this.addAbility(new org.rnd.jmagic.abilities.TapForMana.Final(state, "(U)"));
	}
}
