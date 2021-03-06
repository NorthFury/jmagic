package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Haste;
import org.rnd.jmagic.abilities.keywords.Scavenge;
import org.rnd.jmagic.engine.*;

@Name("Dreg Mangler")
@Types({Type.CREATURE})
@SubTypes({SubType.PLANT, SubType.ZOMBIE})
@ManaCost("1BG")
@Printings({@Printings.Printed(ex = Expansion.RETURN_TO_RAVNICA, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.GREEN, Color.BLACK})
public final class DregMangler extends Card
{
	public DregMangler(GameState state)
	{
		super(state);

		this.setPower(3);
		this.setToughness(3);

		// Haste
		this.addAbility(new Haste(state));

		// Scavenge (3)(B)(G) ((3)(B)(G), Exile this card from your graveyard:
		// Put a number of +1/+1 counters equal to this card's power on target
		// creature. Scavenge only as a sorcery.)
		this.addAbility(new Scavenge(state, "(3)(B)(G)"));
	}
}
