package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Exalted;
import org.rnd.jmagic.abilities.keywords.Reach;
import org.rnd.jmagic.engine.*;

@Name("Court Archers")
@Types({Type.CREATURE})
@SubTypes({SubType.HUMAN, SubType.ARCHER})
@ManaCost("2G")
@Printings({@Printings.Printed(ex = Expansion.SHARDS_OF_ALARA, r = Rarity.COMMON)})
@ColorIdentity({Color.GREEN})
public final class CourtArchers extends Card
{
	public CourtArchers(GameState state)
	{
		super(state);

		this.setPower(1);
		this.setToughness(3);

		this.addAbility(new Reach(state));

		this.addAbility(new Exalted(state));
	}
}
