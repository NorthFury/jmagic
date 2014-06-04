package org.rnd.jmagic.cards;

import org.rnd.jmagic.cardTemplates.RavnicaBounceLand;
import org.rnd.jmagic.engine.*;

@Name("Izzet Boilerworks")
@Types({Type.LAND})
@Printings({@Printings.Printed(ex = Expansion.COMMANDER, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.GUILDPACT, r = Rarity.COMMON)})
@ColorIdentity({Color.BLUE, Color.RED})
public final class IzzetBoilerworks extends RavnicaBounceLand
{
	public IzzetBoilerworks(GameState state)
	{
		super(state, 'U', 'R');
	}
}
