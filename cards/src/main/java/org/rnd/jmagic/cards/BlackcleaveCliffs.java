package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.ScarsTappedLandAbility;
import org.rnd.jmagic.abilities.TapForMana;
import org.rnd.jmagic.engine.*;

@Name("Blackcleave Cliffs")
@Types({Type.LAND})
@Printings({@Printings.Printed(ex = Expansion.SCARS_OF_MIRRODIN, r = Rarity.RARE)})
@ColorIdentity({Color.BLACK, Color.RED})
public final class BlackcleaveCliffs extends Card
{
	public BlackcleaveCliffs(GameState state)
	{
		super(state);

		// Blackcleave Cliffs enters the battlefield tapped unless you control
		// two or fewer other lands.
		this.addAbility(new ScarsTappedLandAbility(state, "Blackcleave Cliffs"));

		// (T): Add (B) or (R) to your mana pool.
		this.addAbility(new TapForMana.Final(state, "(BR)"));
	}
}
