package org.rnd.jmagic.cards;

import org.rnd.jmagic.cardTemplates.FetchLand;
import org.rnd.jmagic.engine.*;

@Name("Scalding Tarn")
@Types({Type.LAND})
@Printings({@Printings.Printed(ex = Expansion.ZENDIKAR, r = Rarity.RARE)})
@ColorIdentity({})
public final class ScaldingTarn extends FetchLand
{
	public ScaldingTarn(GameState state)
	{
		super(state, SubType.ISLAND, SubType.MOUNTAIN);
	}
}
