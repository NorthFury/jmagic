package org.rnd.jmagic.cards;

import org.rnd.jmagic.cardTemplates.OnslaughtCyclingLand;
import org.rnd.jmagic.engine.*;

@Name("Forgotten Cave")
@Types({Type.LAND})
@Printings({@Printings.Printed(ex = Expansion.COMMANDER, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.ONSLAUGHT, r = Rarity.COMMON)})
@ColorIdentity({Color.RED})
public final class ForgottenCave extends OnslaughtCyclingLand
{
	public ForgottenCave(GameState state)
	{
		super(state, "(R)");
	}
}
