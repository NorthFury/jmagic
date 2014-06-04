package org.rnd.jmagic.cards;

import org.rnd.jmagic.cardTemplates.OnslaughtCyclingLand;
import org.rnd.jmagic.engine.*;

@Name("Secluded Steppe")
@Types({Type.LAND})
@Printings({@Printings.Printed(ex = Expansion.COMMANDER, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.ONSLAUGHT, r = Rarity.COMMON)})
@ColorIdentity({Color.WHITE})
public final class SecludedSteppe extends OnslaughtCyclingLand
{
	public SecludedSteppe(GameState state)
	{
		super(state, "(W)");
	}
}
