package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.abilities.keywords.Infect;
import org.rnd.jmagic.engine.*;

@Name("Tine Shrike")
@Types({Type.CREATURE})
@SubTypes({SubType.BIRD})
@ManaCost("3W")
@Printings({@Printings.Printed(ex = Expansion.MIRRODIN_BESIEGED, r = Rarity.COMMON)})
@ColorIdentity({Color.WHITE})
public final class TineShrike extends Card
{
	public TineShrike(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(1);

		// Flying
		this.addAbility(new Flying(state));

		// Infect (This creature deals damage to creatures in the form of -1/-1
		// counters and to players in the form of poison counters.)
		this.addAbility(new Infect(state));
	}
}
