package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Cycling;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Rebuild")
@Types({Type.INSTANT})
@ManaCost("2U")
@Printings({@Printings.Printed(ex = Expansion.URZAS_LEGACY, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.BLUE})
public final class Rebuild extends Card
{
	public Rebuild(GameState state)
	{
		super(state);

		// Return all artifacts to their owners' hands.
		this.addEffect(bounce(ArtifactPermanents.instance(), "Return all artifacts to their owners' hands."));

		// Cycling (2) ((2), Discard this card: Draw a card.)
		this.addAbility(new Cycling(state, "(2)"));
	}
}
