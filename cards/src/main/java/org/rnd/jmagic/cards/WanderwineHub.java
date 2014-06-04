package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.RevealOrThisEntersTapped;
import org.rnd.jmagic.abilities.TapForMana;
import org.rnd.jmagic.engine.*;

@Name("Wanderwine Hub")
@Types({Type.LAND})
@Printings({@Printings.Printed(ex = Expansion.LORWYN, r = Rarity.RARE)})
@ColorIdentity({Color.BLUE, Color.WHITE})
public final class WanderwineHub extends Card
{
	public WanderwineHub(GameState state)
	{
		super(state);

		// As Wanderwine Hub enters the battlefield, you may reveal a Merfolk
		// card from your hand. If you don't, Wanderwine Hub enters the
		// battlefield tapped.
		this.addAbility(new RevealOrThisEntersTapped(state, this.getName(), SubType.MERFOLK));

		// (T): Add (W) or (U) to your mana pool.
		this.addAbility(new TapForMana.Final(state, "(WU)"));
	}
}
