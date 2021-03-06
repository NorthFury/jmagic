package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.AttacksEachTurnIfAble;
import org.rnd.jmagic.abilities.keywords.Cycling;
import org.rnd.jmagic.engine.*;

@Name("Monstrous Carabid")
@Types({Type.CREATURE})
@SubTypes({SubType.INSECT})
@ManaCost("3BR")
@Printings({@Printings.Printed(ex = Expansion.ALARA_REBORN, r = Rarity.COMMON)})
@ColorIdentity({Color.BLACK, Color.RED})
public final class MonstrousCarabid extends Card
{
	public MonstrousCarabid(GameState state)
	{
		super(state);

		this.setPower(4);
		this.setToughness(4);

		// Monstrous Carabid attacks each turn if able.
		this.addAbility(new AttacksEachTurnIfAble(state, this.getName()));

		// Cycling ((b/r)) (((b/r)), Discard this card: Draw a card.)
		this.addAbility(new Cycling(state, "(BR)"));
	}
}
