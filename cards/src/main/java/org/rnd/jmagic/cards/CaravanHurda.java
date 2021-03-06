package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Lifelink;
import org.rnd.jmagic.engine.*;

@Name("Caravan Hurda")
@Types({Type.CREATURE})
@SubTypes({SubType.GIANT})
@ManaCost("4W")
@Printings({@Printings.Printed(ex = Expansion.ZENDIKAR, r = Rarity.COMMON)})
@ColorIdentity({Color.WHITE})
public final class CaravanHurda extends Card
{
	public CaravanHurda(GameState state)
	{
		super(state);

		this.setPower(1);
		this.setToughness(5);

		this.addAbility(new Lifelink(state));
	}
}
