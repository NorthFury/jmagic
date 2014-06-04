package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.EntersTheBattlefieldWithAPlusOnePlusOneCounterForEachTimeItWasKicked;
import org.rnd.jmagic.abilities.keywords.Kicker;
import org.rnd.jmagic.abilities.keywords.Landwalk;
import org.rnd.jmagic.engine.*;

@Name("Enclave Elite")
@Types({Type.CREATURE})
@SubTypes({SubType.SOLDIER, SubType.MERFOLK})
@ManaCost("2U")
@Printings({@Printings.Printed(ex = Expansion.WORLDWAKE, r = Rarity.COMMON)})
@ColorIdentity({Color.BLUE})
public final class EnclaveElite extends Card
{
	public EnclaveElite(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(2);

		// Multikicker (1)(U) (You may pay an additional (1)(U) any number of
		// times as you cast this spell.)
		Kicker kicker = new Kicker(state, true, "(1)(U)");
		this.addAbility(kicker);

		// Islandwalk
		this.addAbility(new Landwalk.Islandwalk(state));

		// Enclave Elite enters the battlefield with a +1/+1 counter on it for
		// each time it was kicked.
		this.addAbility(new EntersTheBattlefieldWithAPlusOnePlusOneCounterForEachTimeItWasKicked(state, "Enclave Elite", kicker.costCollections[0]));
	}
}
