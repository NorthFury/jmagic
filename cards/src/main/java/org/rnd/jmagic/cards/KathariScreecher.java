package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.abilities.keywords.Unearth;
import org.rnd.jmagic.engine.*;

@Name("Kathari Screecher")
@Types({Type.CREATURE})
@SubTypes({SubType.BIRD, SubType.SOLDIER})
@ManaCost("2U")
@Printings({@Printings.Printed(ex = Expansion.SHARDS_OF_ALARA, r = Rarity.COMMON)})
@ColorIdentity({Color.BLUE})
public final class KathariScreecher extends Card
{
	public KathariScreecher(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(2);

		this.addAbility(new Flying(state));

		this.addAbility(new Unearth(state, "(2)(U)"));
	}
}
