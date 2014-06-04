package org.rnd.jmagic.cards;

import org.rnd.jmagic.cardTemplates.InvasionLand;
import org.rnd.jmagic.engine.*;

@Name("Irrigation Ditch")
@Types({Type.LAND})
@Printings({@Printings.Printed(ex = Expansion.INVASION, r = Rarity.COMMON)})
@ColorIdentity({Color.BLUE, Color.WHITE, Color.GREEN})
public final class IrrigationDitch extends InvasionLand
{
	public IrrigationDitch(GameState state)
	{
		super(state, Color.WHITE);
	}
}
