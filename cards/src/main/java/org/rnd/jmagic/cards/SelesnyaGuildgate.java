package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.EntersTheBattlefieldTapped;
import org.rnd.jmagic.abilities.TapForMana;
import org.rnd.jmagic.engine.*;

@Name("Selesnya Guildgate")
@Types({Type.LAND})
@SubTypes({SubType.GATE})
@Printings({@Printings.Printed(ex = Expansion.RETURN_TO_RAVNICA, r = Rarity.COMMON)})
@ColorIdentity({Color.WHITE, Color.GREEN})
public final class SelesnyaGuildgate extends Card
{
	public SelesnyaGuildgate(GameState state)
	{
		super(state);

		// Selesnya Guildgate enters the battlefield tapped.
		this.addAbility(new EntersTheBattlefieldTapped(state, this.getName()));

		// (T): Add (G) or (W) to your mana pool.
		this.addAbility(new TapForMana.Final(state, "(GW)"));
	}
}
