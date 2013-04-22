package org.rnd.jmagic.cards;

import org.rnd.jmagic.engine.*;

@Name("Woolly Thoctar")
@Types({Type.CREATURE})
@SubTypes({SubType.BEAST})
@ManaCost("RGW")
@Printings({@Printings.Printed(ex = Expansion.SHARDS_OF_ALARA, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.WHITE, Color.GREEN, Color.RED})
public final class WoollyThoctar extends Card
{
	public WoollyThoctar(GameState state)
	{
		super(state);

		this.setPower(5);
		this.setToughness(4);
	}
}
