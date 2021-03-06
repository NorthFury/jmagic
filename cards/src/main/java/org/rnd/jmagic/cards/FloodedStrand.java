package org.rnd.jmagic.cards;

import org.rnd.jmagic.cardTemplates.FetchLand;
import org.rnd.jmagic.engine.*;

@Name("Flooded Strand")
@Types({Type.LAND})
@Printings({@Printings.Printed(ex = Expansion.ONSLAUGHT, r = Rarity.RARE)})
@ColorIdentity({})
public final class FloodedStrand extends FetchLand
{
	public FloodedStrand(GameState state)
	{
		super(state, SubType.PLAINS, SubType.ISLAND);
	}
}
