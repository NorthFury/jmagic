package org.rnd.jmagic.cards;

import org.rnd.jmagic.cardTemplates.ShadowmoorDualLand;
import org.rnd.jmagic.engine.*;

@Name("Flooded Grove")
@Types({Type.LAND})
@Printings({@Printings.Printed(ex = Expansion.EVENTIDE, r = Rarity.RARE)})
@ColorIdentity({Color.BLUE, Color.GREEN})
public final class FloodedGrove extends ShadowmoorDualLand
{
	public FloodedGrove(GameState state)
	{
		super(state, "G", "U");
	}
}
