package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.abilities.keywords.Level;
import org.rnd.jmagic.abilities.keywords.LevelUp;
import org.rnd.jmagic.abilities.keywords.Vigilance;
import org.rnd.jmagic.engine.*;

@Name("Knight of Cliffhaven")
@Types({Type.CREATURE})
@SubTypes({SubType.KNIGHT, SubType.KOR})
@ManaCost("1W")
@Printings({@Printings.Printed(ex = Expansion.RISE_OF_THE_ELDRAZI, r = Rarity.COMMON)})
@ColorIdentity({Color.WHITE})
public final class KnightofCliffhaven extends Card
{
	public KnightofCliffhaven(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(2);

		// Level up (3) ((3): Put a level counter on this. Level up only as a
		// sorcery.)
		this.addAbility(new LevelUp(state, "(3)"));

		// LEVEL 1-3
		// 2/3
		// Flying
		this.addAbility(new Level(state, 1, 3, 2, 3, "Flying", Flying.class));

		// LEVEL 4+
		// 4/4
		// Flying, vigilance
		this.addAbility(new Level(state, 4, 4, 4, "Flying; Vigilance", Flying.class, Vigilance.class));
	}
}
