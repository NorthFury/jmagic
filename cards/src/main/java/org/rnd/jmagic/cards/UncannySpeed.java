package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Haste;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Uncanny Speed")
@Types({Type.INSTANT})
@ManaCost("1R")
@Printings({@Printings.Printed(ex = Expansion.AVACYN_RESTORED, r = Rarity.COMMON)})
@ColorIdentity({Color.RED})
public final class UncannySpeed extends Card
{
	public UncannySpeed(GameState state)
	{
		super(state);

		// Target creature gets +3/+0 and gains haste until end of turn.
		SetGenerator target = targetedBy(this.addTarget(CreaturePermanents.instance(), "target creature"));
		this.addEffect(ptChangeAndAbilityUntilEndOfTurn(target, +3, +0, "Target creature gets +3/+0 and gains haste until end of turn.", Haste.class));
	}
}
