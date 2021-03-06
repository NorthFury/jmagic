package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Overload;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Cyclonic Rift")
@Types({Type.INSTANT})
@ManaCost("1U")
@Printings({@Printings.Printed(ex = Expansion.RETURN_TO_RAVNICA, r = Rarity.RARE)})
@ColorIdentity({Color.BLUE})
public final class CyclonicRift extends Card
{
	public CyclonicRift(GameState state)
	{
		super(state);

		// Return target nonland permanent you don't control to its owner's
		// hand.
		SetGenerator target = targetedBy(this.addTarget(RelativeComplement.instance(Permanents.instance(), Union.instance(HasType.instance(Type.LAND), ControlledBy.instance(You.instance()))), "target nonland permanent you don't control"));
		this.addEffect(bounce(target, "Return target nonland permanent you don't control to its owner's hand."));

		// Overload (6)(U) (You may cast this spell for its overload cost. If
		// you do, change its text by replacing all instances of "target" with
		// "each.")
		this.addAbility(new Overload(state, "(6)(U)"));
	}
}
