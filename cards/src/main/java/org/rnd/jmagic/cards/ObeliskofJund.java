package org.rnd.jmagic.cards;

import org.rnd.jmagic.cardTemplates.ShardsObelisk;
import org.rnd.jmagic.engine.*;

@Name("Obelisk of Jund")
@ManaCost("3")
@Types({Type.ARTIFACT})
@Printings({@Printings.Printed(ex = Expansion.SHARDS_OF_ALARA, r = Rarity.COMMON)})
@ColorIdentity({Color.GREEN, Color.BLACK, Color.RED})
public final class ObeliskofJund extends ShardsObelisk
{
	public ObeliskofJund(GameState state)
	{
		super(state, "(BRG)");
	}
}
