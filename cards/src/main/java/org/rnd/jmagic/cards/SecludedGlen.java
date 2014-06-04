package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.RevealOrThisEntersTapped;
import org.rnd.jmagic.abilities.TapForMana;
import org.rnd.jmagic.engine.*;

@Name("Secluded Glen")
@Types({Type.LAND})
@Printings({@Printings.Printed(ex = Expansion.LORWYN, r = Rarity.RARE)})
@ColorIdentity({Color.BLUE, Color.BLACK})
public final class SecludedGlen extends Card
{
	public SecludedGlen(GameState state)
	{
		super(state);

		// As Secluded Glen enters the battlefield, you may reveal a Faerie card
		// from your hand. If you don't, Secluded Glen enters the battlefield
		// tapped.
		this.addAbility(new RevealOrThisEntersTapped(state, this.getName(), SubType.FAERIE));

		// (T): Add (U) or (B) to your mana pool.
		this.addAbility(new TapForMana.Final(state, "(UB)"));
	}
}
