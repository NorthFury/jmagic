package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.TapForMana;
import org.rnd.jmagic.abilities.keywords.FirstStrike;
import org.rnd.jmagic.engine.*;

@Name("Rakdos Keyrune")
@Types({Type.ARTIFACT})
@ManaCost("3")
@Printings({@Printings.Printed(ex = Expansion.RETURN_TO_RAVNICA, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.BLACK, Color.RED})
public final class RakdosKeyrune extends Card
{
	public static final class RakdosKeyruneAbility1 extends ActivatedAbility
	{
		public RakdosKeyruneAbility1(GameState state)
		{
			super(state, "(B)(R): Rakdos Keyrune becomes a 3/1 black and red Devil artifact creature with first strike until end of turn.");
			this.setManaCost(new ManaPool("(B)(R)"));

			Animator animate = new Animator(ABILITY_SOURCE_OF_THIS, 3, 1);
			animate.addColor(Color.BLACK);
			animate.addColor(Color.RED);
			animate.addSubType(SubType.DEVIL);
			animate.addType(Type.ARTIFACT);
			animate.addAbility(FirstStrike.class);
			this.addEffect(createFloatingEffect("Rakdos Keyrune becomes a 3/1 black and red Devil artifact creature with first strike until end of turn.", animate.getParts()));
		}
	}

	public RakdosKeyrune(GameState state)
	{
		super(state);

		// (T): Add (B) or (R) to your mana pool.
		this.addAbility(new TapForMana.Final(state, "(BR)"));

		// (B)(R): Rakdos Keyrune becomes a 3/1 black and red Devil artifact
		// creature with first strike until end of turn.
		this.addAbility(new RakdosKeyruneAbility1(state));
	}
}
