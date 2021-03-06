package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Unleash;
import org.rnd.jmagic.engine.*;

@Name("Gore-House Chainwalker")
@Types({Type.CREATURE})
@SubTypes({SubType.HUMAN, SubType.WARRIOR})
@ManaCost("1R")
@Printings({@Printings.Printed(ex = Expansion.RETURN_TO_RAVNICA, r = Rarity.COMMON)})
@ColorIdentity({Color.RED})
public final class GoreHouseChainwalker extends Card
{
	public GoreHouseChainwalker(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(1);

		// Unleash (You may have this creature enter the battlefield with a
		// +1/+1 counter on it. It can't block as long as it has a +1/+1 counter
		// on it.)
		this.addAbility(new Unleash(state));
	}
}
