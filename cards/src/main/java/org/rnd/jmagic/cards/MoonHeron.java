package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.engine.*;

@Name("Moon Heron")
@Types({Type.CREATURE})
@SubTypes({SubType.SPIRIT, SubType.BIRD})
@ManaCost("3U")
@Printings({@Printings.Printed(ex = Expansion.INNISTRAD, r = Rarity.COMMON)})
@ColorIdentity({Color.BLUE})
public final class MoonHeron extends Card
{
	public MoonHeron(GameState state)
	{
		super(state);

		this.setPower(3);
		this.setToughness(2);

		// Flying
		this.addAbility(new Flying(state));
	}
}
