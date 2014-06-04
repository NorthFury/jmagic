package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.EntersTheBattlefieldTapped;
import org.rnd.jmagic.abilities.TapFor1;
import org.rnd.jmagic.abilities.keywords.Exalted;
import org.rnd.jmagic.engine.*;

@Name("Cathedral of War")
@Types({Type.LAND})
@Printings({@Printings.Printed(ex = Expansion.MAGIC_2013, r = Rarity.RARE)})
@ColorIdentity({})
public final class CathedralofWar extends Card
{
	public CathedralofWar(GameState state)
	{
		super(state);

		// Cathedral of War enters the battlefield tapped.
		this.addAbility(new EntersTheBattlefieldTapped(state, this.getName()));

		// Exalted (Whenever a creature you control attacks alone, that creature
		// gets +1/+1 until end of turn.)
		this.addAbility(new Exalted(state));

		// (T): Add (1) to your mana pool.
		this.addAbility(new TapFor1(state));
	}
}
