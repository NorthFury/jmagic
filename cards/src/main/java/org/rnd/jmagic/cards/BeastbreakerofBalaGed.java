package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Level;
import org.rnd.jmagic.abilities.keywords.LevelUp;
import org.rnd.jmagic.abilities.keywords.Trample;
import org.rnd.jmagic.engine.*;

@Name("Beastbreaker of Bala Ged")
@Types({Type.CREATURE})
@SubTypes({SubType.WARRIOR, SubType.HUMAN})
@ManaCost("1G")
@Printings({@Printings.Printed(ex = Expansion.RISE_OF_THE_ELDRAZI, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.GREEN})
public final class BeastbreakerofBalaGed extends Card
{
	public BeastbreakerofBalaGed(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(2);

		// Level up (2)(G)
		this.addAbility(new LevelUp(state, "(2)(G)"));

		// LEVEL 1-3
		// 4/4
		this.addAbility(new Level(state, 1, 3, 4, 4));

		// LEVEL 4+
		// 6/6
		// Trample
		this.addAbility(new Level(state, 4, 6, 6, "Trample", Trample.class));
	}
}
