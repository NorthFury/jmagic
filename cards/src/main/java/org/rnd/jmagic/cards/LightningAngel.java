package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.abilities.keywords.Haste;
import org.rnd.jmagic.abilities.keywords.Vigilance;
import org.rnd.jmagic.engine.*;

@Name("Lightning Angel")
@Types({Type.CREATURE})
@SubTypes({SubType.ANGEL})
@ManaCost("1RWU")
@Printings({@Printings.Printed(ex = Expansion.TIME_SPIRAL, r = Rarity.SPECIAL), @Printings.Printed(ex = Expansion.APOCALYPSE, r = Rarity.RARE)})
@ColorIdentity({Color.BLUE, Color.WHITE, Color.RED})
public final class LightningAngel extends Card
{
	public LightningAngel(GameState state)
	{
		super(state);

		this.setPower(3);
		this.setToughness(4);

		// Flying, vigilance, haste
		this.addAbility(new Flying(state));
		this.addAbility(new Vigilance(state));
		this.addAbility(new Haste(state));
	}
}
