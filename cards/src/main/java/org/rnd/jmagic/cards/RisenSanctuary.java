package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Vigilance;
import org.rnd.jmagic.engine.*;

@Name("Risen Sanctuary")
@Types({Type.CREATURE})
@SubTypes({SubType.ELEMENTAL})
@ManaCost("5GW")
@Printings({@Printings.Printed(ex = Expansion.RETURN_TO_RAVNICA, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.WHITE, Color.GREEN})
public final class RisenSanctuary extends Card
{
	public RisenSanctuary(GameState state)
	{
		super(state);

		this.setPower(8);
		this.setToughness(8);

		// Vigilance
		this.addAbility(new Vigilance(state));
	}
}
