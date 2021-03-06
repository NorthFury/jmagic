package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.AttacksEachTurnIfAble;
import org.rnd.jmagic.abilities.keywords.Annihilator;
import org.rnd.jmagic.engine.*;

@Name("Ulamog's Crusher")
@Types({Type.CREATURE})
@SubTypes({SubType.ELDRAZI})
@ManaCost("8")
@Printings({@Printings.Printed(ex = Expansion.RISE_OF_THE_ELDRAZI, r = Rarity.COMMON)})
@ColorIdentity({})
public final class UlamogsCrusher extends Card
{
	public UlamogsCrusher(GameState state)
	{
		super(state);

		this.setPower(8);
		this.setToughness(8);

		this.addAbility(new Annihilator.Final(state, 2));

		// Ulamog's Crusher attacks each turn if able.
		this.addAbility(new AttacksEachTurnIfAble(state, this.getName()));
	}
}
