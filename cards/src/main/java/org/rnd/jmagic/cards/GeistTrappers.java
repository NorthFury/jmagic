package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.AbilityIfPaired;
import org.rnd.jmagic.abilities.keywords.Reach;
import org.rnd.jmagic.abilities.keywords.Soulbond;
import org.rnd.jmagic.engine.*;

@Name("Geist Trappers")
@Types({Type.CREATURE})
@SubTypes({SubType.HUMAN, SubType.WARRIOR})
@ManaCost("4G")
@Printings({@Printings.Printed(ex = Expansion.AVACYN_RESTORED, r = Rarity.COMMON)})
@ColorIdentity({Color.GREEN})
public final class GeistTrappers extends Card
{
	public GeistTrappers(GameState state)
	{
		super(state);

		this.setPower(3);
		this.setToughness(5);

		// Soulbond (You may pair this creature with another unpaired creature
		// when either enters the battlefield. They remain paired for as long as
		// you control both of them.)
		this.addAbility(new Soulbond(state));

		// As long as Geist Trappers is paired with another creature, both
		// creatures have reach.
		this.addAbility(new AbilityIfPaired.Final(state, "As long as Geist Trappers is paired with another creature, both creatures have reach.", Reach.class));
	}
}
