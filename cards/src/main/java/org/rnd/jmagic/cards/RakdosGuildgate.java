package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.EntersTheBattlefieldTapped;
import org.rnd.jmagic.abilities.TapForMana;
import org.rnd.jmagic.engine.*;

@Name("Rakdos Guildgate")
@Types({Type.LAND})
@SubTypes({SubType.GATE})
@Printings({@Printings.Printed(ex = Expansion.RETURN_TO_RAVNICA, r = Rarity.COMMON)})
@ColorIdentity({Color.BLACK, Color.RED})
public final class RakdosGuildgate extends Card
{
	public RakdosGuildgate(GameState state)
	{
		super(state);

		// Rakdos Guildgate enters the battlefield tapped.
		this.addAbility(new EntersTheBattlefieldTapped(state, this.getName()));

		// (T): Add (B) or (R) to your mana pool.
		this.addAbility(new TapForMana.Final(state, "(BR)"));
	}
}
