package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.AllSliverCreaturesHave;
import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.engine.*;

@Name("Winged Sliver")
@Types({Type.CREATURE})
@SubTypes({SubType.SLIVER})
@ManaCost("1U")
@Printings({@Printings.Printed(ex = Expansion.TEMPEST, r = Rarity.COMMON)})
@ColorIdentity({Color.BLUE})
public final class WingedSliver extends Card
{
	public WingedSliver(GameState state)
	{
		super(state);

		this.setPower(1);
		this.setToughness(1);

		// All Sliver creatures have flying.
		this.addAbility(new AllSliverCreaturesHave(state, Flying.class, "All Sliver creatures have flying."));
	}
}
