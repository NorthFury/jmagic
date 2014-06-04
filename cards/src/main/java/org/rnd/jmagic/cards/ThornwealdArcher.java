package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Deathtouch;
import org.rnd.jmagic.abilities.keywords.Reach;
import org.rnd.jmagic.engine.*;

@Name("Thornweald Archer")
@Types({Type.CREATURE})
@SubTypes({SubType.ELF, SubType.ARCHER})
@ManaCost("1G")
@Printings({@Printings.Printed(ex = Expansion.FUTURE_SIGHT, r = Rarity.COMMON)})
@ColorIdentity({Color.GREEN})
public final class ThornwealdArcher extends Card
{
	public ThornwealdArcher(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(1);

		this.addAbility(new Reach(state));
		this.addAbility(new Deathtouch(state));
	}
}
