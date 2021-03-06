package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.engine.*;

@Name("Zephyr Sprite")
@Types({Type.CREATURE})
@SubTypes({SubType.FAERIE})
@ManaCost("U")
@Printings({@Printings.Printed(ex = Expansion.MAGIC_2010, r = Rarity.COMMON)})
@ColorIdentity({Color.BLUE})
public final class ZephyrSprite extends Card
{
	public ZephyrSprite(GameState state)
	{
		super(state);

		this.setPower(1);
		this.setToughness(1);

		this.addAbility(new Flying(state));
	}
}
