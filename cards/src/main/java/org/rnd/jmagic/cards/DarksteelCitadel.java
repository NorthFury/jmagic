package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.TapFor1;
import org.rnd.jmagic.abilities.keywords.Indestructible;
import org.rnd.jmagic.engine.*;

@Name("Darksteel Citadel")
@Types({Type.LAND, Type.ARTIFACT})
@Printings({@Printings.Printed(ex = Expansion.DARKSTEEL, r = Rarity.COMMON)})
@ColorIdentity({})
public final class DarksteelCitadel extends Card
{
	public DarksteelCitadel(GameState state)
	{
		super(state);

		// Darksteel Citadel is indestructible. ("Destroy" effects and lethal
		// damage don't destroy it.)
		this.addAbility(new Indestructible(state));

		// (T): Add (1) to your mana pool.
		this.addAbility(new TapFor1(state));
	}
}
