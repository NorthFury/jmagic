package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Lifelink;
import org.rnd.jmagic.engine.*;

@Name("Trained Caracal")
@Types({Type.CREATURE})
@SubTypes({SubType.CAT})
@ManaCost("W")
@Printings({@Printings.Printed(ex = Expansion.RETURN_TO_RAVNICA, r = Rarity.COMMON)})
@ColorIdentity({Color.WHITE})
public final class TrainedCaracal extends Card
{
	public TrainedCaracal(GameState state)
	{
		super(state);

		this.setPower(1);
		this.setToughness(1);

		// Lifelink (Damage dealt by this creature also causes you to gain that
		// much life.)
		this.addAbility(new Lifelink(state));
	}
}
