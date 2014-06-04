package org.rnd.jmagic.cards;

import org.rnd.jmagic.cardTemplates.FetchLand;
import org.rnd.jmagic.engine.*;

@Name("Polluted Delta")
@Types({Type.LAND})
@Printings({@Printings.Printed(ex = Expansion.ONSLAUGHT, r = Rarity.RARE)})
@ColorIdentity({})
public final class PollutedDelta extends FetchLand
{
	public PollutedDelta(GameState state)
	{
		super(state, SubType.ISLAND, SubType.SWAMP);
	}
}
