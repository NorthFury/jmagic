package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.TapForMana;
import org.rnd.jmagic.engine.*;

@Name("Greenweaver Druid")
@Types({Type.CREATURE})
@SubTypes({SubType.ELF, SubType.DRUID})
@ManaCost("2G")
@Printings({@Printings.Printed(ex = Expansion.ZENDIKAR, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.GREEN})
public final class GreenweaverDruid extends Card
{
	public GreenweaverDruid(GameState state)
	{
		super(state);

		this.setPower(1);
		this.setToughness(1);

		// (T): Add (G)(G) to your mana pool.
		this.addAbility(new TapForMana.Final(state, "(G)(G)"));
	}
}
