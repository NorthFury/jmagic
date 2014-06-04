package org.rnd.jmagic.cards;

import org.rnd.jmagic.cardTemplates.RavnicaBounceLand;
import org.rnd.jmagic.engine.*;

@Name("Orzhov Basilica")
@Types({Type.LAND})
@Printings({@Printings.Printed(ex = Expansion.COMMANDER, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.GUILDPACT, r = Rarity.COMMON)})
@ColorIdentity({Color.WHITE, Color.BLACK})
public final class OrzhovBasilica extends RavnicaBounceLand
{
	public OrzhovBasilica(GameState state)
	{
		super(state, 'W', 'B');
	}
}
