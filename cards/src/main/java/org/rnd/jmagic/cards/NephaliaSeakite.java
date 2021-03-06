package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Flash;
import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.engine.*;

@Name("Nephalia Seakite")
@Types({Type.CREATURE})
@SubTypes({SubType.BIRD})
@ManaCost("3U")
@Printings({@Printings.Printed(ex = Expansion.DARK_ASCENSION, r = Rarity.COMMON)})
@ColorIdentity({Color.BLUE})
public final class NephaliaSeakite extends Card
{
	public NephaliaSeakite(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(3);

		// Flash (You may cast this spell any time you could cast an instant.)
		this.addAbility(new Flash(state));

		// Flying
		this.addAbility(new Flying(state));
	}
}
