package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.DoubleStrike;
import org.rnd.jmagic.engine.*;

@Name("Fencing Ace")
@Types({Type.CREATURE})
@SubTypes({SubType.SOLDIER, SubType.HUMAN})
@ManaCost("1W")
@Printings({@Printings.Printed(ex = Expansion.RETURN_TO_RAVNICA, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.WHITE})
public final class FencingAce extends Card
{
	public FencingAce(GameState state)
	{
		super(state);

		this.setPower(1);
		this.setToughness(1);

		// Double strike (This creature deals both first-strike and regular
		// combat damage.)
		this.addAbility(new DoubleStrike(state));
	}
}
