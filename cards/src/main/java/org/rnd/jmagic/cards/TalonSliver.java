package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.AllSliverCreaturesHave;
import org.rnd.jmagic.abilities.keywords.FirstStrike;
import org.rnd.jmagic.engine.*;

@Name("Talon Sliver")
@Types({Type.CREATURE})
@SubTypes({SubType.SLIVER})
@ManaCost("1W")
@Printings({@Printings.Printed(ex = Expansion.TEMPEST, r = Rarity.COMMON)})
@ColorIdentity({Color.WHITE})
public final class TalonSliver extends Card
{
	public TalonSliver(GameState state)
	{
		super(state);

		this.setPower(1);
		this.setToughness(1);

		// All Sliver creatures have first strike.
		this.addAbility(new AllSliverCreaturesHave(state, FirstStrike.class, "All Sliver creatures have first strike."));
	}
}
