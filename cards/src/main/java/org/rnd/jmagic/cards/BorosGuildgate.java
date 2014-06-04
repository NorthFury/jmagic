package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.EntersTheBattlefieldTapped;
import org.rnd.jmagic.abilities.TapForMana;
import org.rnd.jmagic.engine.*;

@Name("Boros Guildgate")
@Types({Type.LAND})
@SubTypes({SubType.GATE})
@Printings({@Printings.Printed(ex = Expansion.GATECRASH, r = Rarity.COMMON)})
@ColorIdentity({Color.WHITE, Color.RED})
public final class BorosGuildgate extends Card
{
	public BorosGuildgate(GameState state)
	{
		super(state);

		// Boros Guildgate enters the battlefield tapped.
		this.addAbility(new EntersTheBattlefieldTapped(state, this.getName()));

		// (T): Add (R) or (W) to your mana pool.
		this.addAbility(new TapForMana.Final(state, "(RW)"));
	}
}
