package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Hexproof;
import org.rnd.jmagic.engine.*;

@Name("Plated Slagwurm")
@Types({Type.CREATURE})
@SubTypes({SubType.WURM})
@ManaCost("4GGG")
@Printings({@Printings.Printed(ex = Expansion.MIRRODIN, r = Rarity.RARE)})
@ColorIdentity({Color.GREEN})
public final class PlatedSlagwurm extends Card
{
	public PlatedSlagwurm(GameState state)
	{
		super(state);

		this.setPower(8);
		this.setToughness(8);

		this.addAbility(new Hexproof(state));
	}
}
