package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.abilities.keywords.Haste;
import org.rnd.jmagic.engine.*;

@Name("Skyknight Legionnaire")
@Types({Type.CREATURE})
@SubTypes({SubType.HUMAN, SubType.KNIGHT})
@ManaCost("1RW")
@Printings({@Printings.Printed(ex = Expansion.GATECRASH, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.RAVNICA, r = Rarity.COMMON)})
@ColorIdentity({Color.WHITE, Color.RED})
public final class SkyknightLegionnaire extends Card
{
	public SkyknightLegionnaire(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(2);

		// Flying, haste
		this.addAbility(new Flying(state));
		this.addAbility(new Haste(state));
	}
}
