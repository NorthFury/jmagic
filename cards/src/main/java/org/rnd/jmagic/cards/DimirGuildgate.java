package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.EntersTheBattlefieldTapped;
import org.rnd.jmagic.abilities.TapForMana;
import org.rnd.jmagic.engine.*;

@Name("Dimir Guildgate")
@Types({Type.LAND})
@SubTypes({SubType.GATE})
@Printings({@Printings.Printed(ex = Expansion.GATECRASH, r = Rarity.COMMON)})
@ColorIdentity({Color.BLUE, Color.BLACK})
public final class DimirGuildgate extends Card
{
	public DimirGuildgate(GameState state)
	{
		super(state);

		// Dimir Guildgate enters the battlefield tapped.
		this.addAbility(new EntersTheBattlefieldTapped(state, this.getName()));

		// (T): Add (U) or (B) to your mana pool.
		this.addAbility(new TapForMana.Final(state, "(UB)"));
	}
}
