package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.FirstStrike;
import org.rnd.jmagic.abilities.keywords.Infect;
import org.rnd.jmagic.engine.*;

@Name("Razor Swine")
@Types({Type.CREATURE})
@SubTypes({SubType.BOAR})
@ManaCost("2R")
@Printings({@Printings.Printed(ex = Expansion.NEW_PHYREXIA, r = Rarity.COMMON)})
@ColorIdentity({Color.RED})
public final class RazorSwine extends Card
{
	public RazorSwine(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(1);

		// First strike
		this.addAbility(new FirstStrike(state));

		// Infect (This creature deals damage to creatures in the form of -1/-1
		// counters and to players in the form of poison counters.)
		this.addAbility(new Infect(state));
	}
}
