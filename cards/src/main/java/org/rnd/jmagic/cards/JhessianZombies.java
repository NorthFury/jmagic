package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Fear;
import org.rnd.jmagic.abilities.keywords.TypeCycling;
import org.rnd.jmagic.engine.*;

@Name("Jhessian Zombies")
@Types({Type.CREATURE})
@SubTypes({SubType.ZOMBIE})
@ManaCost("4UB")
@Printings({@Printings.Printed(ex = Expansion.ALARA_REBORN, r = Rarity.COMMON)})
@ColorIdentity({Color.BLUE, Color.BLACK})
public final class JhessianZombies extends Card
{
	public JhessianZombies(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(4);

		// Fear (This creature can't be blocked except by artifact creatures
		// and/or black creatures.)

		// Islandcycling (2), swampcycling (2) ((2), Discard this card: Search
		// your library for an Island or Swamp card, reveal it, and put it into
		// your hand. Then shuffle your library.)

		this.addAbility(new Fear(state));

		this.addAbility(new TypeCycling.IslandCycling(state, "(2)"));
		this.addAbility(new TypeCycling.SwampCycling(state, "(2)"));
	}
}
