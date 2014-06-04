package org.rnd.jmagic.cards;

import org.rnd.jmagic.cardTemplates.ShardsObelisk;
import org.rnd.jmagic.engine.*;

@Name("Obelisk of Esper")
@ManaCost("3")
@Types({Type.ARTIFACT})
@Printings({@Printings.Printed(ex = Expansion.SHARDS_OF_ALARA, r = Rarity.COMMON)})
@ColorIdentity({Color.BLUE, Color.WHITE, Color.BLACK})
public final class ObeliskofEsper extends ShardsObelisk
{
	public ObeliskofEsper(GameState state)
	{
		super(state, "(WUB)");
	}
}
