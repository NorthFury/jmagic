package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.CantBeCountered;
import org.rnd.jmagic.engine.*;

@Name("Volcanic Fallout")
@Types({Type.INSTANT})
@ManaCost("1RR")
@Printings({@Printings.Printed(ex = Expansion.CONFLUX, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.RED})
public final class VolcanicFallout extends Card
{
	public VolcanicFallout(GameState state)
	{
		super(state);

		// Volcanic Fallout can't be countered.
		this.addAbility(new CantBeCountered(state, this.getName()));

		// Volcanic Fallout deals 2 damage to each creature and each player.
		this.addEffect(spellDealDamage(2, CREATURES_AND_PLAYERS, "Volcanic Fallout deals 2 damage to each creature and each player."));
	}
}
