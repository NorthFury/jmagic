package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.EntersTheBattlefieldTapped;
import org.rnd.jmagic.abilities.Regenerate;
import org.rnd.jmagic.abilities.TapForB;
import org.rnd.jmagic.engine.*;

@Name("Spawning Pool")
@Types({Type.LAND})
@Printings({@Printings.Printed(ex = Expansion.TENTH_EDITION, r = Rarity.UNCOMMON), @Printings.Printed(ex = Expansion.URZAS_LEGACY, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.BLACK})
public final class SpawningPool extends Card
{
	public static final class AnimatePool extends ActivatedAbility
	{
		public static final class RegenerateB extends Regenerate
		{
			public RegenerateB(GameState state)
			{
				super(state, "(B)", "this creature");
			}
		}

		public AnimatePool(GameState state)
		{
			super(state, "(1)(B): Spawning Pool becomes a 1/1 black Skeleton creature with \"(B): Regenerate this creature\" until end of turn. It's still a land.");

			this.setManaCost(new ManaPool("1B"));

			Animator animator = new Animator(ABILITY_SOURCE_OF_THIS, 1, 1);
			animator.addColor(Color.BLACK);
			animator.addSubType(SubType.SKELETON);
			animator.addAbility(RegenerateB.class);
			this.addEffect(createFloatingEffect("Spawning Pool becomes a 1/1 black Skeleton creature with \"(B): Regenerate this creature\" until end of turn. It's still a land.", animator.getParts()));
		}
	}

	public SpawningPool(GameState state)
	{
		super(state);

		this.addAbility(new EntersTheBattlefieldTapped(state, this.getName()));
		this.addAbility(new TapForB(state));
		this.addAbility(new AnimatePool(state));
	}
}
