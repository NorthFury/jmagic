package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.abilities.keywords.Infect;
import org.rnd.jmagic.engine.*;

@Name("Plague Stinger")
@Types({Type.CREATURE})
@SubTypes({SubType.INSECT, SubType.HORROR})
@ManaCost("1B")
@Printings({@Printings.Printed(ex = Expansion.SCARS_OF_MIRRODIN, r = Rarity.COMMON)})
@ColorIdentity({Color.BLACK})
public final class PlagueStinger extends Card
{
	public PlagueStinger(GameState state)
	{
		super(state);

		this.setPower(1);
		this.setToughness(1);

		// Flying
		this.addAbility(new Flying(state));

		// Infect (This creature deals damage to creatures in the form of -1/-1
		// counters and to players in the form of poison counters.)
		this.addAbility(new Infect(state));
	}
}
