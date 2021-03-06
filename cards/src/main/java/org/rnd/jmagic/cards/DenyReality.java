package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Cascade;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Deny Reality")
@Types({Type.SORCERY})
@ManaCost("3UB")
@Printings({@Printings.Printed(ex = Expansion.ALARA_REBORN, r = Rarity.COMMON)})
@ColorIdentity({Color.BLUE, Color.BLACK})
public final class DenyReality extends Card
{
	public DenyReality(GameState state)
	{
		super(state);

		// Cascade
		this.addAbility(new Cascade(state));

		// Return target permanent to its owner's hand.
		Target target = this.addTarget(Permanents.instance(), "target permanent");
		this.addEffect(bounce(targetedBy(target), "Return target permanent to its owner's hand."));
	}
}
