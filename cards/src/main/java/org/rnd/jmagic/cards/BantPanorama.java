package org.rnd.jmagic.cards;

import org.rnd.jmagic.cardTemplates.*;
import org.rnd.jmagic.engine.*;

@Name("Bant Panorama")
@Types({Type.LAND})
@Printings({@Printings.Printed(ex = Expansion.SHARDS_OF_ALARA, r = Rarity.COMMON)})
@ColorIdentity({})
public final class BantPanorama extends ShardsPanorama
{
	public BantPanorama(GameState state)
	{
		super(state, SubType.FOREST, SubType.PLAINS, SubType.ISLAND);
	}
}
