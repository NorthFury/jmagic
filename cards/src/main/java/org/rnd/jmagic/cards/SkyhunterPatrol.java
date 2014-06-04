package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.FirstStrike;
import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.engine.*;

@Name("Skyhunter Patrol")
@Types({Type.CREATURE})
@SubTypes({SubType.CAT, SubType.KNIGHT})
@ManaCost("2WW")
@Printings({@Printings.Printed(ex = Expansion.TENTH_EDITION, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.MIRRODIN, r = Rarity.COMMON)})
@ColorIdentity({Color.WHITE})
public final class SkyhunterPatrol extends Card
{
	public SkyhunterPatrol(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(3);

		this.addAbility(new Flying(state));
		this.addAbility(new FirstStrike(state));
	}
}
