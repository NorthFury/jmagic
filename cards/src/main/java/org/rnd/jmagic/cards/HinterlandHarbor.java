package org.rnd.jmagic.cards;

import org.rnd.jmagic.cardTemplates.Magic2010DualLand;
import org.rnd.jmagic.engine.*;

@Name("Hinterland Harbor")
@Types({Type.LAND})
@Printings({@Printings.Printed(ex = Expansion.INNISTRAD, r = Rarity.RARE)})
@ColorIdentity({Color.BLUE, Color.GREEN})
public final class HinterlandHarbor extends Magic2010DualLand
{
	public HinterlandHarbor(GameState state)
	{
		super(state, SubType.FOREST, SubType.ISLAND);
	}
}
