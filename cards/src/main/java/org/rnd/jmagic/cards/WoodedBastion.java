package org.rnd.jmagic.cards;

import org.rnd.jmagic.engine.*;

@Name("Wooded Bastion")
@Types({Type.LAND})
@Printings({@Printings.Printed(ex = Expansion.SHADOWMOOR, r = Rarity.RARE)})
@ColorIdentity({Color.WHITE, Color.GREEN})
public final class WoodedBastion extends org.rnd.jmagic.cardTemplates.ShadowmoorDualLand
{
	public WoodedBastion(GameState state)
	{
		super(state, "G", "W");
	}
}