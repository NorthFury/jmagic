package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.EntersTheBattlefieldWithAPlusOnePlusOneCounterForEachTimeItWasKicked;
import org.rnd.jmagic.abilities.keywords.Haste;
import org.rnd.jmagic.abilities.keywords.Kicker;
import org.rnd.jmagic.engine.*;

@Name("Skitter of Lizards")
@Types({Type.CREATURE})
@SubTypes({SubType.LIZARD})
@ManaCost("R")
@Printings({@Printings.Printed(ex = Expansion.WORLDWAKE, r = Rarity.COMMON)})
@ColorIdentity({Color.RED})
public final class SkitterofLizards extends Card
{
	public SkitterofLizards(GameState state)
	{
		super(state);

		this.setPower(1);
		this.setToughness(1);

		// Multikicker (1)(R) (You may pay an additional (1)(R) any number of
		// times as you cast this spell.)
		Kicker kicker = new Kicker(state, true, "(1)(R)");
		this.addAbility(kicker);

		// Haste
		this.addAbility(new Haste(state));

		// Skitter of Lizards enters the battlefield with a +1/+1 counter on it
		// for each time it was kicked.
		this.addAbility(new EntersTheBattlefieldWithAPlusOnePlusOneCounterForEachTimeItWasKicked(state, "Skitter of Lizards", kicker.costCollections[0]));
	}
}
