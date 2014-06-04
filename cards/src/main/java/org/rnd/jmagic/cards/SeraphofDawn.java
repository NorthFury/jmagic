package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.abilities.keywords.Lifelink;
import org.rnd.jmagic.engine.*;

@Name("Seraph of Dawn")
@Types({Type.CREATURE})
@SubTypes({SubType.ANGEL})
@ManaCost("2WW")
@Printings({@Printings.Printed(ex = Expansion.AVACYN_RESTORED, r = Rarity.COMMON)})
@ColorIdentity({Color.WHITE})
public final class SeraphofDawn extends Card
{
	public SeraphofDawn(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(4);

		// Flying
		this.addAbility(new Flying(state));

		// Lifelink (Damage dealt by this creature also causes you to gain that
		// much life.)
		this.addAbility(new Lifelink(state));
	}
}
