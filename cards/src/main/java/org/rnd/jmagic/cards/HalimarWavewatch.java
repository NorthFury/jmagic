package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Landwalk;
import org.rnd.jmagic.abilities.keywords.Level;
import org.rnd.jmagic.abilities.keywords.LevelUp;
import org.rnd.jmagic.engine.*;

@Name("Halimar Wavewatch")
@Types({Type.CREATURE})
@SubTypes({SubType.SOLDIER, SubType.MERFOLK})
@ManaCost("1U")
@Printings({@Printings.Printed(ex = Expansion.RISE_OF_THE_ELDRAZI, r = Rarity.COMMON)})
@ColorIdentity({Color.BLUE})
public final class HalimarWavewatch extends Card
{
	public HalimarWavewatch(GameState state)
	{
		super(state);

		this.setPower(0);
		this.setToughness(3);

		// Level up (2) ((2): Put a level counter on this. Level up only as a
		// sorcery.)
		this.addAbility(new LevelUp(state, "(2)"));

		// LEVEL 1-4
		// 0/6
		this.addAbility(new Level(state, 1, 4, 0, 6));

		// LEVEL 5+
		// 6/6
		// Islandwalk
		this.addAbility(new Level(state, 5, 6, 6, "Islandwalk", Landwalk.Islandwalk.class));
	}
}
