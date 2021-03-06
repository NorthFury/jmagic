package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Trostani's Judgment")
@Types({Type.INSTANT})
@ManaCost("5W")
@Printings({@Printings.Printed(ex = Expansion.RETURN_TO_RAVNICA, r = Rarity.COMMON)})
@ColorIdentity({Color.WHITE})
public final class TrostanisJudgment extends Card
{
	public TrostanisJudgment(GameState state)
	{
		super(state);

		// Exile target creature,
		SetGenerator target = targetedBy(this.addTarget(CreaturePermanents.instance(), "target creature"));
		this.addEffect(exile(target, "Exile target creature,"));

		// then populate.
		this.addEffect(populate("then populate."));
	}
}
