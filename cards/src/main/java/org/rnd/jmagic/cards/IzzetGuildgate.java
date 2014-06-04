package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.EntersTheBattlefieldTapped;
import org.rnd.jmagic.abilities.TapForMana;
import org.rnd.jmagic.engine.*;

@Name("Izzet Guildgate")
@Types({Type.LAND})
@SubTypes({SubType.GATE})
@Printings({@Printings.Printed(ex = Expansion.RETURN_TO_RAVNICA, r = Rarity.COMMON)})
@ColorIdentity({Color.BLUE, Color.RED})
public final class IzzetGuildgate extends Card
{
	public IzzetGuildgate(GameState state)
	{
		super(state);

		// Izzet Guildgate enters the battlefield tapped.
		this.addAbility(new EntersTheBattlefieldTapped(state, this.getName()));

		// (T): Add (U) or (R) to your mana pool.
		this.addAbility(new TapForMana.Final(state, "(UR)"));
	}
}
