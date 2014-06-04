package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Bloodthirst;
import org.rnd.jmagic.engine.*;

@Name("Bloodrage Vampire")
@Types({Type.CREATURE})
@SubTypes({SubType.VAMPIRE})
@ManaCost("2B")
@Printings({@Printings.Printed(ex = Expansion.MAGIC_2012, r = Rarity.COMMON)})
@ColorIdentity({Color.BLACK})
public final class BloodrageVampire extends Card
{
	public BloodrageVampire(GameState state)
	{
		super(state);

		this.setPower(3);
		this.setToughness(1);

		// Bloodthirst 1 (If an opponent was dealt damage this turn, this
		// creature enters the battlefield with a +1/+1 counter on it.)
		this.addAbility(new Bloodthirst.Final(state, 1));
	}
}
