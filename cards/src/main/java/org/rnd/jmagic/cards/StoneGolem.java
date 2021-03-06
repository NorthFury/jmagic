package org.rnd.jmagic.cards;

import org.rnd.jmagic.engine.*;

@Name("Stone Golem")
@Types({Type.CREATURE, Type.ARTIFACT})
@SubTypes({SubType.GOLEM})
@ManaCost("5")
@Printings({@Printings.Printed(ex = Expansion.MAGIC_2011, r = Rarity.UNCOMMON)})
@ColorIdentity({})
public final class StoneGolem extends Card
{
	public StoneGolem(GameState state)
	{
		super(state);

		this.setPower(4);
		this.setToughness(4);
	}
}
