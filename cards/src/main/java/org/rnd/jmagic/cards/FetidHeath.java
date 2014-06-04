package org.rnd.jmagic.cards;

import org.rnd.jmagic.cardTemplates.ShadowmoorDualLand;
import org.rnd.jmagic.engine.*;

@Name("Fetid Heath")
@Types({Type.LAND})
@Printings({@Printings.Printed(ex = Expansion.EVENTIDE, r = Rarity.RARE)})
@ColorIdentity({Color.WHITE, Color.BLACK})
public final class FetidHeath extends ShadowmoorDualLand
{
	public FetidHeath(GameState state)
	{
		super(state, "W", "B");
	}
}
