package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.HarbingerAbility;
import org.rnd.jmagic.engine.*;

@Name("Boggart Harbinger")
@Types({Type.CREATURE})
@SubTypes({SubType.SHAMAN, SubType.GOBLIN})
@ManaCost("2B")
@Printings({@Printings.Printed(ex = Expansion.LORWYN, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.BLACK})
public final class BoggartHarbinger extends Card
{
	public BoggartHarbinger(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(1);

		this.addAbility(new HarbingerAbility(state, this.getName(), SubType.GOBLIN));
	}
}
