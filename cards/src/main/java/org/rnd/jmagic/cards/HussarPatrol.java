package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Flash;
import org.rnd.jmagic.abilities.keywords.Vigilance;
import org.rnd.jmagic.engine.*;

@Name("Hussar Patrol")
@Types({Type.CREATURE})
@SubTypes({SubType.HUMAN, SubType.KNIGHT})
@ManaCost("2WU")
@Printings({@Printings.Printed(ex = Expansion.RETURN_TO_RAVNICA, r = Rarity.COMMON)})
@ColorIdentity({Color.BLUE, Color.WHITE})
public final class HussarPatrol extends Card
{
	public HussarPatrol(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(4);

		// Flash (You may cast this spell any time you could cast an instant.)
		this.addAbility(new Flash(state));

		// Vigilance
		this.addAbility(new Vigilance(state));
	}
}
