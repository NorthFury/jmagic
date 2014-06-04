package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Landwalk;
import org.rnd.jmagic.engine.*;

@Name("Bog Tatters")
@Types({Type.CREATURE})
@SubTypes({SubType.WRAITH})
@ManaCost("4B")
@Printings({@Printings.Printed(ex = Expansion.ZENDIKAR, r = Rarity.COMMON)})
@ColorIdentity({Color.BLACK})
public final class BogTatters extends Card
{
	public BogTatters(GameState state)
	{
		super(state);

		this.setPower(4);
		this.setToughness(2);

		this.addAbility(new Landwalk.Swampwalk(state));
	}
}
