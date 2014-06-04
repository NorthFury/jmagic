package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.DoubleStrike;
import org.rnd.jmagic.abilities.keywords.FirstStrike;
import org.rnd.jmagic.abilities.keywords.Level;
import org.rnd.jmagic.abilities.keywords.LevelUp;
import org.rnd.jmagic.engine.*;

@Name("Student of Warfare")
@Types({Type.CREATURE})
@SubTypes({SubType.KNIGHT, SubType.HUMAN})
@ManaCost("W")
@Printings({@Printings.Printed(ex = Expansion.RISE_OF_THE_ELDRAZI, r = Rarity.RARE)})
@ColorIdentity({Color.WHITE})
public final class StudentofWarfare extends Card
{
	public StudentofWarfare(GameState state)
	{
		super(state);

		this.setPower(1);
		this.setToughness(1);

		// Level up (W)
		this.addAbility(new LevelUp(state, "(W)"));

		// LEVEL 2-6
		// 3/3
		// First strike
		this.addAbility(new Level(state, 2, 6, 3, 3, "First strike", FirstStrike.class));

		// LEVEL 7+
		// 4/4
		// Double strike
		this.addAbility(new Level(state, 7, 4, 4, "Double strike", DoubleStrike.class));
	}
}
