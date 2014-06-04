package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.TapForAnyColor;
import org.rnd.jmagic.engine.*;

@Name("Alloy Myr")
@Types({Type.ARTIFACT, Type.CREATURE})
@SubTypes({SubType.MYR})
@ManaCost("3")
@Printings({@Printings.Printed(ex = Expansion.NEW_PHYREXIA, r = Rarity.UNCOMMON)})
@ColorIdentity({})
public final class AlloyMyr extends Card
{
	public AlloyMyr(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(2);

		// (T): Add one mana of any color to your mana pool.
		this.addAbility(new TapForAnyColor(state));
	}
}
