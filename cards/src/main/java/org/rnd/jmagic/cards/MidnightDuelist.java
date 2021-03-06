package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Protection;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Midnight Duelist")
@Types({Type.CREATURE})
@SubTypes({SubType.SOLDIER, SubType.HUMAN})
@ManaCost("W")
@Printings({@Printings.Printed(ex = Expansion.AVACYN_RESTORED, r = Rarity.COMMON)})
@ColorIdentity({Color.WHITE})
public final class MidnightDuelist extends Card
{
	public MidnightDuelist(GameState state)
	{
		super(state);

		this.setPower(1);
		this.setToughness(2);

		// Protection from Vampires
		SetGenerator vampires = HasSubType.instance(SubType.VAMPIRE);
		this.addAbility(new Protection.From(state, vampires, "vampires"));
	}
}
