package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.Bloodrush;
import org.rnd.jmagic.abilities.keywords.DoubleStrike;
import org.rnd.jmagic.engine.*;

@Name("Wrecking Ogre")
@Types({Type.CREATURE})
@SubTypes({SubType.OGRE, SubType.WARRIOR})
@ManaCost("4R")
@Printings({@Printings.Printed(ex = Expansion.GATECRASH, r = Rarity.RARE)})
@ColorIdentity({Color.RED})
public final class WreckingOgre extends Card
{
	public WreckingOgre(GameState state)
	{
		super(state);

		this.setPower(3);
		this.setToughness(3);

		// Double strike
		this.addAbility(new DoubleStrike(state));

		// Bloodrush \u2014 (3)(R)(R), Discard Wrecking Ogre: Target attacking
		// creature gets +3/+3 and gains double strike until end of turn.
		this.addAbility(new Bloodrush(state, "(3)(R)(R)", "Wrecking Ogre", +3, +3, "Target attacking creature gets +3/+3 and gains double strike until end of turn.", DoubleStrike.class));
	}
}
