package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.Regenerate;
import org.rnd.jmagic.abilities.keywords.Infect;
import org.rnd.jmagic.engine.*;

@Name("Toxic Nim")
@Types({Type.CREATURE})
@SubTypes({SubType.ZOMBIE})
@ManaCost("4BB")
@Printings({@Printings.Printed(ex = Expansion.NEW_PHYREXIA, r = Rarity.COMMON)})
@ColorIdentity({Color.BLACK})
public final class ToxicNim extends Card
{
	public ToxicNim(GameState state)
	{
		super(state);

		this.setPower(4);
		this.setToughness(1);

		// Infect (This creature deals damage to creatures in the form of -1/-1
		// counters and to players in the form of poison counters.)
		this.addAbility(new Infect(state));

		// (B): Regenerate Toxic Nim.
		this.addAbility(new Regenerate.Final(state, "(B)", "Toxic Nim"));
	}
}
