package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.EntersTheBattlefieldTapped;
import org.rnd.jmagic.abilities.TapForMana;
import org.rnd.jmagic.engine.*;

@Name("Azorius Guildgate")
@Types({Type.LAND})
@SubTypes({SubType.GATE})
@Printings({@Printings.Printed(ex = Expansion.RETURN_TO_RAVNICA, r = Rarity.COMMON)})
@ColorIdentity({Color.BLUE, Color.WHITE})
public final class AzoriusGuildgate extends Card
{
	public AzoriusGuildgate(GameState state)
	{
		super(state);

		// Azorius Guildgate enters the battlefield tapped.
		this.addAbility(new EntersTheBattlefieldTapped(state, this.getName()));

		// (T): Add (W) or (U) to your mana pool.
		this.addAbility(new TapForMana.Final(state, "(WU)"));
	}
}
