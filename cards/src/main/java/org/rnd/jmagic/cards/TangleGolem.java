package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Affinity;
import org.rnd.jmagic.engine.*;

@Name("Tangle Golem")
@Types({Type.CREATURE, Type.ARTIFACT})
@SubTypes({SubType.GOLEM})
@ManaCost("7")
@Printings({@Printings.Printed(ex = Expansion.DARKSTEEL, r = Rarity.COMMON)})
@ColorIdentity({})
public final class TangleGolem extends Card
{
	public TangleGolem(GameState state)
	{
		super(state);

		this.setPower(5);
		this.setToughness(4);

		// Affinity for Forests
		this.addAbility(new Affinity.ForForests(state));
	}
}
