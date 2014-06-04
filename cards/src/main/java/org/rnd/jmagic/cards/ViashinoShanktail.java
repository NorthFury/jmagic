package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.Bloodrush;
import org.rnd.jmagic.abilities.keywords.FirstStrike;
import org.rnd.jmagic.engine.*;

@Name("Viashino Shanktail")
@Types({Type.CREATURE})
@SubTypes({SubType.WARRIOR, SubType.VIASHINO})
@ManaCost("3R")
@Printings({@Printings.Printed(ex = Expansion.GATECRASH, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.RED})
public final class ViashinoShanktail extends Card
{
	public ViashinoShanktail(GameState state)
	{
		super(state);

		this.setPower(3);
		this.setToughness(1);

		// First strike
		this.addAbility(new FirstStrike(state));

		// Bloodrush \u2014 (2)(R), Discard Viashino Shanktail: Target attacking
		// creature gets +3/+1 and gains first strike until end of turn.
		this.addAbility(new Bloodrush(state, "(2)(R)", "Viashino Shanktail", +3, +1, "Target attacking creature gets +3/+1 and gains first strike until end of turn.", FirstStrike.class));
	}
}
