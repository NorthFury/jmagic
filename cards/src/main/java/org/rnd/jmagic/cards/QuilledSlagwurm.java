package org.rnd.jmagic.cards;

import org.rnd.jmagic.engine.*;

@Name("Quilled Slagwurm")
@Types({Type.CREATURE})
@SubTypes({SubType.WURM})
@ManaCost("4GGG")
@Printings({@Printings.Printed(ex = Expansion.MIRRODIN_BESIEGED, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.GREEN})
public final class QuilledSlagwurm extends Card
{
	public QuilledSlagwurm(GameState state)
	{
		super(state);

		this.setPower(8);
		this.setToughness(8);
	}
}
