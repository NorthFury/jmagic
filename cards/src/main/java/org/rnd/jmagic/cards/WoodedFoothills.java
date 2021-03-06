package org.rnd.jmagic.cards;

import org.rnd.jmagic.cardTemplates.FetchLand;
import org.rnd.jmagic.engine.*;

@Name("Wooded Foothills")
@Types({Type.LAND})
@Printings({@Printings.Printed(ex = Expansion.ONSLAUGHT, r = Rarity.RARE)})
@ColorIdentity({})
public final class WoodedFoothills extends FetchLand
{
	public WoodedFoothills(GameState state)
	{
		super(state, SubType.MOUNTAIN, SubType.FOREST);
	}
}
