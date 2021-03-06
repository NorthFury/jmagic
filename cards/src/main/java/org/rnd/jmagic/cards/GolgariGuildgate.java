package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.EntersTheBattlefieldTapped;
import org.rnd.jmagic.abilities.TapForMana;
import org.rnd.jmagic.engine.*;

@Name("Golgari Guildgate")
@Types({Type.LAND})
@SubTypes({SubType.GATE})
@Printings({@Printings.Printed(ex = Expansion.RETURN_TO_RAVNICA, r = Rarity.COMMON)})
@ColorIdentity({Color.GREEN, Color.BLACK})
public final class GolgariGuildgate extends Card
{
	public GolgariGuildgate(GameState state)
	{
		super(state);

		// Golgari Guildgate enters the battlefield tapped.
		this.addAbility(new EntersTheBattlefieldTapped(state, this.getName()));

		// (T): Add (B) or (G) to your mana pool.
		this.addAbility(new TapForMana.Final(state, "(BG)"));
	}
}
