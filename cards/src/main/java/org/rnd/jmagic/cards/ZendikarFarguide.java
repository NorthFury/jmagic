package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Landwalk;
import org.rnd.jmagic.engine.*;

@Name("Zendikar Farguide")
@Types({Type.CREATURE})
@SubTypes({SubType.ELEMENTAL})
@ManaCost("4G")
@Printings({@Printings.Printed(ex = Expansion.ZENDIKAR, r = Rarity.COMMON)})
@ColorIdentity({Color.GREEN})
public final class ZendikarFarguide extends Card
{
	public ZendikarFarguide(GameState state)
	{
		super(state);

		this.setPower(3);
		this.setToughness(3);

		this.addAbility(new Landwalk.Forestwalk(state));
	}
}
