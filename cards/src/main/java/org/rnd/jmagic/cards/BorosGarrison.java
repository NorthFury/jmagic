package org.rnd.jmagic.cards;

import org.rnd.jmagic.cardTemplates.RavnicaBounceLand;
import org.rnd.jmagic.engine.*;

@Name("Boros Garrison")
@Types({Type.LAND})
@Printings({@Printings.Printed(ex = Expansion.COMMANDER, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.RAVNICA, r = Rarity.COMMON)})
@ColorIdentity({Color.WHITE, Color.RED})
public final class BorosGarrison extends RavnicaBounceLand
{
	public BorosGarrison(GameState state)
	{
		super(state, 'R', 'W');
	}
}
