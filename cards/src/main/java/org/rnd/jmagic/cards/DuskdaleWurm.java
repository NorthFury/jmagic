package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Trample;
import org.rnd.jmagic.engine.*;

@Name("Duskdale Wurm")
@Types({Type.CREATURE})
@SubTypes({SubType.WURM})
@ManaCost("5GG")
@Printings({@Printings.Printed(ex = Expansion.MAGIC_2013, r = Rarity.UNCOMMON), @Printings.Printed(ex = Expansion.MAGIC_2011, r = Rarity.UNCOMMON), @Printings.Printed(ex = Expansion.EVENTIDE, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.GREEN})
public final class DuskdaleWurm extends Card
{
	public DuskdaleWurm(GameState state)
	{
		super(state);

		this.setPower(7);
		this.setToughness(7);

		// Trample (If this creature would assign enough damage to its blockers
		// to destroy them, you may have it assign the rest of its damage to
		// defending player or planeswalker.)
		this.addAbility(new Trample(state));
	}
}
