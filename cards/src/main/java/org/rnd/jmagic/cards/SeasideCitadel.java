package org.rnd.jmagic.cards;

import org.rnd.jmagic.cardTemplates.ETBTLand;
import org.rnd.jmagic.engine.*;

@Name("Seaside Citadel")
@Types({Type.LAND})
@Printings({@Printings.Printed(ex = Expansion.SHARDS_OF_ALARA, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.BLUE, Color.WHITE, Color.GREEN})
public final class SeasideCitadel extends ETBTLand
{
	public SeasideCitadel(GameState state)
	{
		super(state, "(GWU)");
	}
}
