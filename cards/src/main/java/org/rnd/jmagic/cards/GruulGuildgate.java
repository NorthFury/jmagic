package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.EntersTheBattlefieldTapped;
import org.rnd.jmagic.abilities.TapForMana;
import org.rnd.jmagic.engine.*;

@Name("Gruul Guildgate")
@Types({Type.LAND})
@SubTypes({SubType.GATE})
@Printings({@Printings.Printed(ex = Expansion.GATECRASH, r = Rarity.COMMON)})
@ColorIdentity({Color.GREEN, Color.RED})
public final class GruulGuildgate extends Card
{
	public GruulGuildgate(GameState state)
	{
		super(state);

		// Gruul Guildgate enters the battlefield tapped.
		this.addAbility(new EntersTheBattlefieldTapped(state, this.getName()));

		// (T): Add (R) or (G) to your mana pool.
		this.addAbility(new TapForMana.Final(state, "(RG)"));
	}
}
