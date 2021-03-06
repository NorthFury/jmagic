package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Cascade;
import org.rnd.jmagic.engine.*;

@Name("Violent Outburst")
@Types({Type.INSTANT})
@ManaCost("1RG")
@Printings({@Printings.Printed(ex = Expansion.ALARA_REBORN, r = Rarity.COMMON)})
@ColorIdentity({Color.GREEN, Color.RED})
public final class ViolentOutburst extends Card
{
	public ViolentOutburst(GameState state)
	{
		super(state);

		// Cascade
		this.addAbility(new Cascade(state));

		// Creatures you control get +1/+0 until end of turn.
		this.addEffect(ptChangeUntilEndOfTurn(CREATURES_YOU_CONTROL, +1, +0, "Creatures you control get +1/+0 until end of turn."));
	}
}
