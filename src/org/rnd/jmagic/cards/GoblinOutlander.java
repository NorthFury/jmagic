package org.rnd.jmagic.cards;

import org.rnd.jmagic.engine.*;

@Name("Goblin Outlander")
@ManaCost("BR")
@Types({Type.CREATURE})
@SubTypes({SubType.GOBLIN, SubType.SCOUT})
@Printings({@Printings.Printed(ex = Expansion.CONFLUX, r = Rarity.COMMON)})
@ColorIdentity({Color.BLACK, Color.RED})
public final class GoblinOutlander extends org.rnd.jmagic.cardTemplates.Outlander
{
	public GoblinOutlander(GameState state)
	{
		super(state, Color.WHITE);
	}
}
