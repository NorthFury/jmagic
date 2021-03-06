package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.MeleeGetPlusOnePlusOneCounters;
import org.rnd.jmagic.abilities.keywords.Intimidate;
import org.rnd.jmagic.engine.*;

@Name("Heirs of Stromkirk")
@Types({Type.CREATURE})
@SubTypes({SubType.VAMPIRE})
@ManaCost("2RR")
@Printings({@Printings.Printed(ex = Expansion.AVACYN_RESTORED, r = Rarity.COMMON)})
@ColorIdentity({Color.RED})
public final class HeirsofStromkirk extends Card
{
	public HeirsofStromkirk(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(2);

		// Intimidate (This creature can't be blocked except by artifact
		// creatures and/or creatures that share a color with it.)
		this.addAbility(new Intimidate(state));

		// Whenever Heirs of Stromkirk deals combat damage to a player, put a
		// +1/+1 counter on it.
		this.addAbility(new MeleeGetPlusOnePlusOneCounters(state, "Heirs of Stromkirk", 1));
	}
}
