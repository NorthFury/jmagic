package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Flashback;
import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Rolling Temblor")
@Types({Type.SORCERY})
@ManaCost("2R")
@Printings({@Printings.Printed(ex = Expansion.INNISTRAD, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.RED})
public final class RollingTemblor extends Card
{
	public RollingTemblor(GameState state)
	{
		super(state);

		// Rolling Temblor deals 2 damage to each creature without flying.
		this.addEffect(spellDealDamage(2, RelativeComplement.instance(CreaturePermanents.instance(), HasKeywordAbility.instance(Flying.class)), "Rolling Temblor deals 2 damage to each creature without flying."));

		// Flashback (4)(R)(R) (You may cast this card from your graveyard for
		// its flashback cost. Then exile it.)
		this.addAbility(new Flashback(state, "(4)(R)(R)"));
	}
}
