package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Defender;
import org.rnd.jmagic.abilities.keywords.Flash;
import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.engine.*;

@Name("Plumeveil")
@Types({Type.CREATURE})
@SubTypes({SubType.ELEMENTAL})
@ManaCost("(W/U)(W/U)(W/U)")
@Printings({@Printings.Printed(ex = Expansion.COMMANDER, r = Rarity.UNCOMMON), @Printings.Printed(ex = Expansion.SHADOWMOOR, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.BLUE, Color.WHITE})
public final class Plumeveil extends Card
{
	public Plumeveil(GameState state)
	{
		super(state);

		this.setPower(4);
		this.setToughness(4);

		// Flash
		this.addAbility(new Flash(state));

		// Defender, flying
		this.addAbility(new Defender(state));
		this.addAbility(new Flying(state));
	}
}
