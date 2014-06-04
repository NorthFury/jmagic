package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.AllSliverCreaturesHave;
import org.rnd.jmagic.abilities.keywords.Frenzy;
import org.rnd.jmagic.engine.*;

@Name("Frenzy Sliver")
@Types({Type.CREATURE})
@SubTypes({SubType.SLIVER})
@ManaCost("1B")
@Printings({@Printings.Printed(ex = Expansion.FUTURE_SIGHT, r = Rarity.COMMON)})
@ColorIdentity({Color.BLACK})
public final class FrenzySliver extends Card
{
	@Name("Frenzy 1")
	public static final class Frenzy1 extends Frenzy
	{
		public Frenzy1(GameState state)
		{
			super(state, 1);
		}
	}

	public FrenzySliver(GameState state)
	{
		super(state);

		this.setPower(1);
		this.setToughness(1);

		this.addAbility(new AllSliverCreaturesHave(state, Frenzy1.class, "All Sliver creatures have frenzy 1."));
	}

}
