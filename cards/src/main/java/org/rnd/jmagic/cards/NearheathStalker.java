package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Undying;
import org.rnd.jmagic.engine.*;

@Name("Nearheath Stalker")
@Types({Type.CREATURE})
@SubTypes({SubType.ROGUE, SubType.VAMPIRE})
@ManaCost("4R")
@Printings({@Printings.Printed(ex = Expansion.DARK_ASCENSION, r = Rarity.COMMON)})
@ColorIdentity({Color.RED})
public final class NearheathStalker extends Card
{
	public NearheathStalker(GameState state)
	{
		super(state);

		this.setPower(4);
		this.setToughness(1);

		// Undying (When this creature dies, if it had no +1/+1 counters on it,
		// return it to the battlefield under its owner's control with a +1/+1
		// counter on it.)
		this.addAbility(new Undying(state));
	}
}
