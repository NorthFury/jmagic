package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.FirstStrike;
import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.abilities.keywords.Vigilance;
import org.rnd.jmagic.engine.*;

@Name("Shepherd of the Lost")
@Types({Type.CREATURE})
@SubTypes({SubType.ANGEL})
@ManaCost("4W")
@Printings({@Printings.Printed(ex = Expansion.ZENDIKAR, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.WHITE})
public final class ShepherdoftheLost extends Card
{
	public ShepherdoftheLost(GameState state)
	{
		super(state);

		this.setPower(3);
		this.setToughness(3);

		this.addAbility(new Flying(state));
		this.addAbility(new FirstStrike(state));
		this.addAbility(new Vigilance(state));
	}
}
