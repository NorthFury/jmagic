package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.EntersTheBattlefieldTapped;
import org.rnd.jmagic.abilities.TapForMana;
import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.abilities.keywords.Vigilance;
import org.rnd.jmagic.engine.*;

@Name("Celestial Colonnade")
@Types({Type.LAND})
@Printings({@Printings.Printed(ex = Expansion.WORLDWAKE, r = Rarity.RARE)})
@ColorIdentity({Color.BLUE, Color.WHITE})
public final class CelestialColonnade extends Card
{
	public static final class AnimateColonnade extends ActivatedAbility
	{
		public AnimateColonnade(GameState state)
		{
			super(state, "(3)(W)(U): Until end of turn, Celestial Colonnade becomes a 4/4 white and blue Elemental creature with flying and vigilance. It's still a land.");
			this.setManaCost(new ManaPool("3WU"));

			Animator animator = new Animator(ABILITY_SOURCE_OF_THIS, 4, 4);
			animator.addColor(Color.WHITE);
			animator.addColor(Color.BLUE);
			animator.addSubType(SubType.ELEMENTAL);
			animator.addAbility(Flying.class);
			animator.addAbility(Vigilance.class);
			this.addEffect(createFloatingEffect("Until end of turn, Celestial Colonnade becomes a 4/4 white and blue Elemental creature with flying and vigilance. It's still a land.", animator.getParts()));
		}
	}

	public CelestialColonnade(GameState state)
	{
		super(state);

		// Celestial Colonnade enters the battlefield tapped.
		this.addAbility(new EntersTheBattlefieldTapped(state, this.getName()));

		// (T): Add (W) or (U) to your mana pool.
		this.addAbility(new TapForMana.Final(state, "(WU)"));

		// (3)(W)(U): Until end of turn, Celestial Colonnade becomes a 4/4 white
		// and blue Elemental creature with flying and vigilance. It's still a
		// land.
		this.addAbility(new AnimateColonnade(state));
	}
}
