package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.ZendikarAllyCounter;
import org.rnd.jmagic.abilities.keywords.FirstStrike;
import org.rnd.jmagic.abilities.keywords.Vigilance;
import org.rnd.jmagic.engine.*;

@Name("Kazandu Blademaster")
@Types({Type.CREATURE})
@SubTypes({SubType.SOLDIER, SubType.ALLY, SubType.HUMAN})
@ManaCost("WW")
@Printings({@Printings.Printed(ex = Expansion.ZENDIKAR, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.WHITE})
public final class KazanduBlademaster extends Card
{
	public KazanduBlademaster(GameState state)
	{
		super(state);

		this.setPower(1);
		this.setToughness(1);

		this.addAbility(new FirstStrike(state));

		this.addAbility(new Vigilance(state));

		this.addAbility(new ZendikarAllyCounter(state, this.getName()));
	}
}
