package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.ZendikarAllyCounter;
import org.rnd.jmagic.engine.*;

@Name("Oran-Rief Survivalist")
@Types({Type.CREATURE})
@SubTypes({SubType.ALLY, SubType.WARRIOR, SubType.HUMAN})
@ManaCost("1G")
@Printings({@Printings.Printed(ex = Expansion.ZENDIKAR, r = Rarity.COMMON)})
@ColorIdentity({Color.GREEN})
public final class OranRiefSurvivalist extends Card
{
	public OranRiefSurvivalist(GameState state)
	{
		super(state);

		this.setPower(1);
		this.setToughness(1);

		this.addAbility(new ZendikarAllyCounter(state, this.getName()));
	}
}
