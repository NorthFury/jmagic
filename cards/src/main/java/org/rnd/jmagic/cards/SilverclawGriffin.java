package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.FirstStrike;
import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.engine.*;

@Name("Silverclaw Griffin")
@Types({Type.CREATURE})
@SubTypes({SubType.GRIFFIN})
@ManaCost("3WW")
@Printings({@Printings.Printed(ex = Expansion.DARK_ASCENSION, r = Rarity.COMMON)})
@ColorIdentity({Color.WHITE})
public final class SilverclawGriffin extends Card
{
	public SilverclawGriffin(GameState state)
	{
		super(state);

		this.setPower(3);
		this.setToughness(2);

		// Flying, first strike
		this.addAbility(new Flying(state));
		this.addAbility(new FirstStrike(state));
	}
}
