package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Level;
import org.rnd.jmagic.abilities.keywords.LevelUp;
import org.rnd.jmagic.abilities.keywords.Shroud;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Hada Spy Patrol")
@Types({Type.CREATURE})
@SubTypes({SubType.HUMAN, SubType.ROGUE})
@ManaCost("1U")
@Printings({@Printings.Printed(ex = Expansion.RISE_OF_THE_ELDRAZI, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.BLUE})
public final class HadaSpyPatrol extends Card
{
	public static final class HadaSpyPatrolIsUnblockable extends StaticAbility
	{
		public HadaSpyPatrolIsUnblockable(GameState state)
		{
			super(state, "Hada Spy Patrol is unblockable.");

			this.addEffectPart(unblockable(This.instance()));
		}
	}

	public HadaSpyPatrol(GameState state)
	{
		super(state);

		this.setPower(1);
		this.setToughness(1);

		// Level up (2)(U)
		this.addAbility(new LevelUp(state, "(2)(U)"));

		// LEVEL 1-2
		// 2/2
		// Hada Spy Patrol is unblockable.
		this.addAbility(new Level(state, 1, 2, 2, 2, "Hada Spy Patrol is unblockable.", HadaSpyPatrolIsUnblockable.class));

		// LEVEL 3+
		// 3/3
		// Shroud
		// Hada Spy Patrol is unblockable.
		this.addAbility(new Level(state, 3, 3, 3, "Shroud; Hada Spy Patrol is unblockable.", Shroud.class, HadaSpyPatrolIsUnblockable.class));
	}
}
