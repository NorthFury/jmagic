package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.EntersTheBattlefieldWithCounters;
import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Nimbus Swimmer")
@Types({Type.CREATURE})
@SubTypes({SubType.LEVIATHAN})
@ManaCost("XGU")
@Printings({@Printings.Printed(ex = Expansion.GATECRASH, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.BLUE, Color.GREEN})
public final class NimbusSwimmer extends Card
{
	public NimbusSwimmer(GameState state)
	{
		super(state);

		this.setPower(0);
		this.setToughness(0);

		// Flying
		this.addAbility(new Flying(state));

		// Nimbus Swimmer enters the battlefield with X +1/+1 counters on it.
		this.addAbility(new EntersTheBattlefieldWithCounters(state, "Nimbus Swimmer", ValueOfX.instance(This.instance()), "X +1/+1 counters on it", Counter.CounterType.PLUS_ONE_PLUS_ONE));
	}
}
