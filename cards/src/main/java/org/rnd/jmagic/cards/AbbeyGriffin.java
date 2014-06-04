package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.abilities.keywords.Vigilance;
import org.rnd.jmagic.engine.*;

@Name("Abbey Griffin")
@Types({Type.CREATURE})
@SubTypes({SubType.GRIFFIN})
@ManaCost("3W")
@Printings({@Printings.Printed(ex = Expansion.INNISTRAD, r = Rarity.COMMON)})
@ColorIdentity({Color.WHITE})
public final class AbbeyGriffin extends Card
{
	public AbbeyGriffin(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(2);

		// Flying, vigilance
		this.addAbility(new Flying(state));
		this.addAbility(new Vigilance(state));
	}
}
