package org.rnd.jmagic.cards;

import org.rnd.jmagic.cardTemplates.ShadowmoorDualLand;
import org.rnd.jmagic.engine.*;

@Name("Sunken Ruins")
@Types({Type.LAND})
@Printings({@Printings.Printed(ex = Expansion.SHADOWMOOR, r = Rarity.RARE)})
@ColorIdentity({Color.BLUE, Color.BLACK})
public final class SunkenRuins extends ShadowmoorDualLand
{
	public SunkenRuins(GameState state)
	{
		super(state, "U", "B");
	}
}
