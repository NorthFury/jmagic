package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.CantBeCountered;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Obliterate")
@Types({Type.SORCERY})
@ManaCost("6RR")
@Printings({@Printings.Printed(ex = Expansion.EIGHTH_EDITION, r = Rarity.RARE), @Printings.Printed(ex = Expansion.INVASION, r = Rarity.RARE)})
@ColorIdentity({Color.RED})
public final class Obliterate extends Card
{
	public Obliterate(GameState state)
	{
		super(state);

		this.addAbility(new CantBeCountered(state, "Obliterate"));

		this.addEffects(bury(this, Union.instance(Union.instance(ArtifactPermanents.instance(), CreaturePermanents.instance()), LandPermanents.instance()), "Destroy all artifacts, creatures, and lands. They can't be regenerated."));
	}
}
