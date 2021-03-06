package org.rnd.jmagic.cards;

import org.rnd.jmagic.cardTemplates.InvasionLand;
import org.rnd.jmagic.engine.*;

@Name("Ancient Spring")
@Types({Type.LAND})
@Printings({@Printings.Printed(ex = Expansion.INVASION, r = Rarity.COMMON)})
@ColorIdentity({Color.WHITE, Color.BLUE, Color.BLACK})
public final class AncientSpring extends InvasionLand
{
	public AncientSpring(GameState state)
	{
		super(state, Color.BLUE);
	}
}
