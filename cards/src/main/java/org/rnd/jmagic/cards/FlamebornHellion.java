package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.AttacksEachTurnIfAble;
import org.rnd.jmagic.abilities.keywords.Haste;
import org.rnd.jmagic.engine.*;

@Name("Flameborn Hellion")
@Types({Type.CREATURE})
@SubTypes({SubType.HELLION})
@ManaCost("5R")
@Printings({@Printings.Printed(ex = Expansion.SCARS_OF_MIRRODIN, r = Rarity.COMMON)})
@ColorIdentity({Color.RED})
public final class FlamebornHellion extends Card
{
	public FlamebornHellion(GameState state)
	{
		super(state);

		this.setPower(5);
		this.setToughness(4);

		// Haste
		this.addAbility(new Haste(state));

		// Flameborn Hellion attacks each turn if able.
		this.addAbility(new AttacksEachTurnIfAble(state, this.getName()));
	}
}
