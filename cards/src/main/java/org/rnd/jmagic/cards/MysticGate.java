package org.rnd.jmagic.cards;

import org.rnd.jmagic.cardTemplates.ShadowmoorDualLand;
import org.rnd.jmagic.engine.*;

@Name("Mystic Gate")
@Types({Type.LAND})
@Printings({@Printings.Printed(ex = Expansion.SHADOWMOOR, r = Rarity.RARE)})
@ColorIdentity({Color.BLUE, Color.WHITE})
public final class MysticGate extends ShadowmoorDualLand
{
	public MysticGate(GameState state)
	{
		super(state, "W", "U");
	}
}
