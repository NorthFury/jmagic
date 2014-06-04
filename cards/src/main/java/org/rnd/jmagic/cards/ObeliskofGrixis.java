package org.rnd.jmagic.cards;

import org.rnd.jmagic.cardTemplates.ShardsObelisk;
import org.rnd.jmagic.engine.*;

@Name("Obelisk of Grixis")
@ManaCost("3")
@Types({Type.ARTIFACT})
@Printings({@Printings.Printed(ex = Expansion.SHARDS_OF_ALARA, r = Rarity.COMMON)})
@ColorIdentity({Color.BLUE, Color.BLACK, Color.RED})
public final class ObeliskofGrixis extends ShardsObelisk
{
	public ObeliskofGrixis(GameState state)
	{
		super(state, "(UBR)");
	}
}
