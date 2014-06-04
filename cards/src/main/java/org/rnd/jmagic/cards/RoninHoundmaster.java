package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Bushido;
import org.rnd.jmagic.abilities.keywords.Haste;
import org.rnd.jmagic.engine.*;

@Name("Ronin Houndmaster")
@Types({Type.CREATURE})
@SubTypes({SubType.HUMAN, SubType.SAMURAI})
@ManaCost("2R")
@Printings({@Printings.Printed(ex = Expansion.CHAMPIONS_OF_KAMIGAWA, r = Rarity.COMMON)})
@ColorIdentity({Color.RED})
public final class RoninHoundmaster extends Card
{
	public RoninHoundmaster(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(2);

		this.addAbility(new Haste(state));
		this.addAbility(new Bushido(state, 1));
	}
}
