package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.EntersTheBattlefieldTapped;
import org.rnd.jmagic.abilities.TapForR;
import org.rnd.jmagic.abilities.keywords.FirstStrike;
import org.rnd.jmagic.engine.*;

@Name("Ghitu Encampment")
@Types({Type.LAND})
@Printings({@Printings.Printed(ex = Expansion.TENTH_EDITION, r = Rarity.UNCOMMON), @Printings.Printed(ex = Expansion.URZAS_LEGACY, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.RED})
public final class GhituEncampment extends Card
{
	public static final class AnimateEncampment extends ActivatedAbility
	{
		public AnimateEncampment(GameState state)
		{
			super(state, "(1)(R): Ghitu Encampment becomes a 2/1 red Warrior creature with first strike until end of turn. It's still a land.");

			this.setManaCost(new ManaPool("1R"));

			Animator animator = new Animator(ABILITY_SOURCE_OF_THIS, 2, 1);
			animator.addColor(Color.RED);
			animator.addSubType(SubType.WARRIOR);
			animator.addAbility(FirstStrike.class);
			this.addEffect(createFloatingEffect("Ghitu Encampment becomes a 2/1 red Warrior creature with first strike until end of turn. It's still a land.", animator.getParts()));
		}
	}

	public GhituEncampment(GameState state)
	{
		super(state);

		this.addAbility(new EntersTheBattlefieldTapped(state, this.getName()));
		this.addAbility(new TapForR(state));
		this.addAbility(new AnimateEncampment(state));
	}
}
