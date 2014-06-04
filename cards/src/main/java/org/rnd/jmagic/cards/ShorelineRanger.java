package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.abilities.keywords.TypeCycling;
import org.rnd.jmagic.engine.*;

@Name("Shoreline Ranger")
@Types({Type.CREATURE})
@SubTypes({SubType.SOLDIER, SubType.BIRD})
@ManaCost("5U")
@Printings({@Printings.Printed(ex = Expansion.SCOURGE, r = Rarity.COMMON)})
@ColorIdentity({Color.BLUE})
public final class ShorelineRanger extends Card
{
	public ShorelineRanger(GameState state)
	{
		super(state);

		this.setPower(3);
		this.setToughness(4);

		// Flying
		this.addAbility(new Flying(state));

		// Islandcycling (2)
		this.addAbility(new TypeCycling.IslandCycling(state, "(2)"));
	}
}
