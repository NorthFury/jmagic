package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.RevealOrThisEntersTapped;
import org.rnd.jmagic.abilities.TapForManaPain;
import org.rnd.jmagic.engine.*;

@Name("Murmuring Bosk")
@Types({Type.LAND})
@SubTypes({SubType.FOREST})
@Printings({@Printings.Printed(ex = Expansion.MORNINGTIDE, r = Rarity.RARE)})
@ColorIdentity({Color.WHITE, Color.BLACK, Color.GREEN})
public final class MurmuringBosk extends Card
{
	public static final class MurmuringBoskAbility2 extends ActivatedAbility
	{
		public MurmuringBoskAbility2(GameState state)
		{
			super(state, "(T): Add (W) or (B) to your mana pool. Murmuring Bosk deals 1 damage to you.");
			this.costsTap = true;
		}
	}

	public MurmuringBosk(GameState state)
	{
		super(state);

		// As Murmuring Bosk enters the battlefield, you may reveal a Treefolk
		// card from your hand. If you don't, Murmuring Bosk enters the
		// battlefield tapped.
		this.addAbility(new RevealOrThisEntersTapped(state, this.getName(), SubType.TREEFOLK));

		// (T): Add (W) or (B) to your mana pool. Murmuring Bosk deals 1 damage
		// to you.
		this.addAbility(new TapForManaPain(state, this.getName(), "WB"));
	}
}
