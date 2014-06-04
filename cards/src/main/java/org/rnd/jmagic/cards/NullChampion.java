package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.Regenerate;
import org.rnd.jmagic.abilities.keywords.Level;
import org.rnd.jmagic.abilities.keywords.LevelUp;
import org.rnd.jmagic.engine.*;

@Name("Null Champion")
@Types({Type.CREATURE})
@SubTypes({SubType.WARRIOR, SubType.ZOMBIE})
@ManaCost("1B")
@Printings({@Printings.Printed(ex = Expansion.RISE_OF_THE_ELDRAZI, r = Rarity.COMMON)})
@ColorIdentity({Color.BLACK})
public final class NullChampion extends Card
{
	public static final class RegenerateB extends Regenerate
	{
		public RegenerateB(GameState state)
		{
			super(state, "(B)", "Null Champion");
		}
	}

	public NullChampion(GameState state)
	{
		super(state);

		this.setPower(1);
		this.setToughness(1);

		// Level up (3) ((3): Put a level counter on this. Level up only as a
		// sorcery.)
		this.addAbility(new LevelUp(state, "(3)"));

		// LEVEL 1-3
		// 4/2
		this.addAbility(new Level(state, 1, 3, 4, 2));

		// LEVEL 4+
		// 7/3
		// (B): Regenerate Null Champion.
		this.addAbility(new Level(state, 4, 7, 3, "(B): Regenerate Null Champion.", RegenerateB.class));

	}
}
