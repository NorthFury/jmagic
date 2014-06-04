package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.Unblockable;
import org.rnd.jmagic.abilities.keywords.Infect;
import org.rnd.jmagic.engine.*;

@Name("Blighted Agent")
@Types({Type.CREATURE})
@SubTypes({SubType.HUMAN, SubType.ROGUE})
@ManaCost("1U")
@Printings({@Printings.Printed(ex = Expansion.NEW_PHYREXIA, r = Rarity.COMMON)})
@ColorIdentity({Color.BLUE})
public final class BlightedAgent extends Card
{
	public BlightedAgent(GameState state)
	{
		super(state);

		this.setPower(1);
		this.setToughness(1);

		// Infect (This creature deals damage to creatures in the form of -1/-1
		// counters and to players in the form of poison counters.)
		this.addAbility(new Infect(state));

		// Blighted Agent is unblockable.
		this.addAbility(new Unblockable(state, this.getName()));
	}
}
