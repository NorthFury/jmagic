package org.rnd.jmagic.cards;

import org.rnd.jmagic.cardTemplates.ShadowmoorDualLand;
import org.rnd.jmagic.engine.*;

@Name("Twilight Mire")
@Types({Type.LAND})
@Printings({@Printings.Printed(ex = Expansion.EVENTIDE, r = Rarity.RARE)})
@ColorIdentity({Color.GREEN, Color.BLACK})
public final class TwilightMire extends ShadowmoorDualLand
{
	public TwilightMire(GameState state)
	{
		super(state, "B", "G");
	}
}
