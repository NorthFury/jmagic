package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Deathtouch;
import org.rnd.jmagic.abilities.keywords.FirstStrike;
import org.rnd.jmagic.abilities.keywords.Level;
import org.rnd.jmagic.abilities.keywords.LevelUp;
import org.rnd.jmagic.engine.*;

@Name("Nirkana Cutthroat")
@Types({Type.CREATURE})
@SubTypes({SubType.WARRIOR, SubType.VAMPIRE})
@ManaCost("2B")
@Printings({@Printings.Printed(ex = Expansion.RISE_OF_THE_ELDRAZI, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.BLACK})
public final class NirkanaCutthroat extends Card
{
	public NirkanaCutthroat(GameState state)
	{
		super(state);

		this.setPower(3);
		this.setToughness(2);

		// Level up (2)(B) ((2)(B): Put a level counter on this. Level up only
		// as a sorcery.)
		this.addAbility(new LevelUp(state, "(2)(B)"));

		// LEVEL 1-2
		// 4/3
		// Deathtouch
		this.addAbility(new Level(state, 1, 2, 4, 3, "Deathtouch", Deathtouch.class));

		// LEVEL 3+
		// 5/4
		// First strike, deathtouch
		this.addAbility(new Level(state, 3, 5, 4, "First strike, deathtouch", FirstStrike.class, Deathtouch.class));
	}
}
