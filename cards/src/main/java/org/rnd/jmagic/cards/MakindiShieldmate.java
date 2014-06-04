package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.ZendikarAllyCounter;
import org.rnd.jmagic.abilities.keywords.Defender;
import org.rnd.jmagic.engine.*;

@Name("Makindi Shieldmate")
@Types({Type.CREATURE})
@SubTypes({SubType.KOR, SubType.SOLDIER, SubType.ALLY})
@ManaCost("2W")
@Printings({@Printings.Printed(ex = Expansion.ZENDIKAR, r = Rarity.COMMON)})
@ColorIdentity({Color.WHITE})
public final class MakindiShieldmate extends Card
{
	public MakindiShieldmate(GameState state)
	{
		super(state);

		this.setPower(0);
		this.setToughness(3);

		this.addAbility(new Defender(state));

		this.addAbility(new ZendikarAllyCounter(state, this.getName()));
	}
}
