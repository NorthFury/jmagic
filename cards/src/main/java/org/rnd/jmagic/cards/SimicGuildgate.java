package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.EntersTheBattlefieldTapped;
import org.rnd.jmagic.abilities.TapForMana;
import org.rnd.jmagic.engine.*;

@Name("Simic Guildgate")
@Types({Type.LAND})
@SubTypes({SubType.GATE})
@Printings({@Printings.Printed(ex = Expansion.GATECRASH, r = Rarity.COMMON)})
@ColorIdentity({Color.BLUE, Color.GREEN})
public final class SimicGuildgate extends Card
{
	public SimicGuildgate(GameState state)
	{
		super(state);

		// Simic Guildgate enters the battlefield tapped.
		this.addAbility(new EntersTheBattlefieldTapped(state, this.getName()));

		// (T): Add (G) or (U) to your mana pool.
		this.addAbility(new TapForMana.Final(state, "(GU)"));
	}
}
