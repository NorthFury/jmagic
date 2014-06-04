package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.RevealOrThisEntersTapped;
import org.rnd.jmagic.abilities.TapForMana;
import org.rnd.jmagic.engine.*;

@Name("Auntie's Hovel")
@Types({Type.LAND})
@Printings({@Printings.Printed(ex = Expansion.LORWYN, r = Rarity.RARE)})
@ColorIdentity({Color.BLACK, Color.RED})
public final class AuntiesHovel extends Card
{
	public AuntiesHovel(GameState state)
	{
		super(state);

		this.addAbility(new RevealOrThisEntersTapped(state, this.getName(), SubType.GOBLIN));
		this.addAbility(new TapForMana.Final(state, "(BR)"));
	}
}
