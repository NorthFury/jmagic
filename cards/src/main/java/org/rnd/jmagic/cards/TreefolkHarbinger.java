package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.HarbingerAbility;
import org.rnd.jmagic.engine.*;

@Name("Treefolk Harbinger")
@Types({Type.CREATURE})
@SubTypes({SubType.DRUID, SubType.TREEFOLK})
@ManaCost("G")
@Printings({@Printings.Printed(ex = Expansion.LORWYN, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.GREEN})
public final class TreefolkHarbinger extends Card
{
	public TreefolkHarbinger(GameState state)
	{
		super(state);

		this.setPower(0);
		this.setToughness(3);

		// When Treefolk Harbinger enters the battlefield, you may search your
		// library for a Treefolk or Forest card, reveal it, then shuffle your
		// library and put that card on top of it.
		this.addAbility(new HarbingerAbility(state, this.getName(), SubType.TREEFOLK, SubType.FOREST));
	}
}
