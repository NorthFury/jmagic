package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Flash;
import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.engine.*;

@Name("Skyline Predator")
@Types({Type.CREATURE})
@SubTypes({SubType.DRAKE})
@ManaCost("4UU")
@Printings({@Printings.Printed(ex = Expansion.RETURN_TO_RAVNICA, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.BLUE})
public final class SkylinePredator extends Card
{
	public SkylinePredator(GameState state)
	{
		super(state);

		this.setPower(3);
		this.setToughness(4);

		// Flash (You may cast this spell any time you could cast an instant.)
		this.addAbility(new Flash(state));

		// Flying
		this.addAbility(new Flying(state));
	}
}
