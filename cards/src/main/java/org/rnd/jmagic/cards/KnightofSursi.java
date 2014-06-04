package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Flanking;
import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.abilities.keywords.Suspend;
import org.rnd.jmagic.engine.*;

@Name("Knight of Sursi")
@Types({Type.CREATURE})
@SubTypes({SubType.HUMAN, SubType.KNIGHT})
@ManaCost("3W")
@Printings({@Printings.Printed(ex = Expansion.FUTURE_SIGHT, r = Rarity.COMMON)})
@ColorIdentity({Color.WHITE})
public final class KnightofSursi extends Card
{
	public KnightofSursi(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(2);

		// Flying; flanking
		this.addAbility(new Flying(state));
		this.addAbility(new Flanking(state));

		// Suspend 3\u2014(W)
		this.addAbility(new Suspend(state, 3, "(W)"));
	}
}
