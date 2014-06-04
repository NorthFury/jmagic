package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.CreaturePermanents;

@Name("Damnation")
@Types({Type.SORCERY})
@ManaCost("2BB")
@Printings({@Printings.Printed(ex = Expansion.PLANAR_CHAOS, r = Rarity.RARE)})
@ColorIdentity({Color.BLACK})
public final class Damnation extends Card
{
	public Damnation(GameState state)
	{
		super(state);

		this.addEffects(bury(this, CreaturePermanents.instance(), "Destroy all creatures. They can't be regenerated."));
	}
}
