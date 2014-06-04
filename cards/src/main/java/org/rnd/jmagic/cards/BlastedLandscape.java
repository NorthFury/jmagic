package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.TapFor1;
import org.rnd.jmagic.abilities.keywords.Cycling;
import org.rnd.jmagic.engine.*;

@Name("Blasted Landscape")
@Types({Type.LAND})
@Printings({@Printings.Printed(ex = Expansion.URZAS_SAGA, r = Rarity.UNCOMMON)})
@ColorIdentity({})
public final class BlastedLandscape extends Card
{
	public BlastedLandscape(GameState state)
	{
		super(state);

		this.addAbility(new TapFor1(state));

		this.addAbility(new Cycling(state, "(2)"));
	}
}
