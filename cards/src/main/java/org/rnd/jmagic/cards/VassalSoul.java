package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.engine.*;

@Name("Vassal Soul")
@Types({Type.CREATURE})
@SubTypes({SubType.SPIRIT})
@ManaCost("1(W/U)(W/U)")
@Printings({@Printings.Printed(ex = Expansion.RETURN_TO_RAVNICA, r = Rarity.COMMON)})
@ColorIdentity({Color.BLUE, Color.WHITE})
public final class VassalSoul extends Card
{
	public VassalSoul(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(2);

		// Flying
		this.addAbility(new Flying(state));
	}
}
