package org.rnd.jmagic.cards;

import org.rnd.jmagic.cardTemplates.ShadowmoorDualLand;
import org.rnd.jmagic.engine.*;

@Name("Rugged Prairie")
@Types({Type.LAND})
@Printings({@Printings.Printed(ex = Expansion.EVENTIDE, r = Rarity.RARE)})
@ColorIdentity({Color.WHITE, Color.RED})
public final class RuggedPrairie extends ShadowmoorDualLand
{
	public RuggedPrairie(GameState state)
	{
		super(state, "R", "W");
	}
}
