package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.AbilityIfPaired;
import org.rnd.jmagic.abilities.keywords.FirstStrike;
import org.rnd.jmagic.abilities.keywords.Soulbond;
import org.rnd.jmagic.engine.*;

@Name("Hanweir Lancer")
@Types({Type.CREATURE})
@SubTypes({SubType.HUMAN, SubType.KNIGHT})
@ManaCost("2R")
@Printings({@Printings.Printed(ex = Expansion.AVACYN_RESTORED, r = Rarity.COMMON)})
@ColorIdentity({Color.RED})
public final class HanweirLancer extends Card
{
	public HanweirLancer(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(2);

		// Soulbond (You may pair this creature with another unpaired creature
		// when either enters the battlefield. They remain paired for as long as
		// you control both of them.)
		this.addAbility(new Soulbond(state));

		// As long as Hanweir Lancer is paired with another creature, both
		// creatures have first strike.
		this.addAbility(new AbilityIfPaired.Final(state, "As long as Hanweir Lancer is paired with another creature, both creatures have first strike.", FirstStrike.class));
	}
}
