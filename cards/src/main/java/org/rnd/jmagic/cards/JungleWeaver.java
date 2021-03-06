package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Cycling;
import org.rnd.jmagic.abilities.keywords.Reach;
import org.rnd.jmagic.engine.*;

@Name("Jungle Weaver")
@Types({Type.CREATURE})
@SubTypes({SubType.SPIDER})
@ManaCost("5GG")
@Printings({@Printings.Printed(ex = Expansion.SHARDS_OF_ALARA, r = Rarity.COMMON)})
@ColorIdentity({Color.GREEN})
public final class JungleWeaver extends Card
{
	public JungleWeaver(GameState state)
	{
		super(state);

		this.setPower(5);
		this.setToughness(6);

		this.addAbility(new Reach(state));

		this.addAbility(new Cycling(state, "(2)"));
	}
}
