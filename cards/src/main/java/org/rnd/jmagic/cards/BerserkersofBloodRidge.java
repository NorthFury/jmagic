package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.AttacksEachTurnIfAble;
import org.rnd.jmagic.engine.*;

@Name("Berserkers of Blood Ridge")
@Types({Type.CREATURE})
@SubTypes({SubType.BERSERKER, SubType.HUMAN})
@ManaCost("4R")
@Printings({@Printings.Printed(ex = Expansion.MAGIC_2011, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.MAGIC_2010, r = Rarity.COMMON)})
@ColorIdentity({Color.RED})
public final class BerserkersofBloodRidge extends Card
{
	public BerserkersofBloodRidge(GameState state)
	{
		super(state);

		this.setPower(4);
		this.setToughness(4);

		// Berserkers of Blood Ridge attacks each turn if able.
		this.addAbility(new AttacksEachTurnIfAble(state, this.getName()));
	}
}
