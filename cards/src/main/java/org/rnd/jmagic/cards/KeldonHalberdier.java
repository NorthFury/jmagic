package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.FirstStrike;
import org.rnd.jmagic.abilities.keywords.Suspend;
import org.rnd.jmagic.engine.*;

@Name("Keldon Halberdier")
@Types({Type.CREATURE})
@SubTypes({SubType.WARRIOR, SubType.HUMAN})
@ManaCost("4R")
@Printings({@Printings.Printed(ex = Expansion.TIME_SPIRAL, r = Rarity.COMMON)})
@ColorIdentity({Color.RED})
public final class KeldonHalberdier extends Card
{
	public KeldonHalberdier(GameState state)
	{
		super(state);

		this.setPower(4);
		this.setToughness(1);

		this.addAbility(new FirstStrike(state));

		this.addAbility(new Suspend(state, 4, "(R)"));
	}
}
