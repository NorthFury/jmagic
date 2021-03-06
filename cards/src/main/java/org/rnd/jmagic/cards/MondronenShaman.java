package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.Werewolves;
import org.rnd.jmagic.engine.*;

@Name("Mondronen Shaman")
@Types({Type.CREATURE})
@SubTypes({SubType.SHAMAN, SubType.HUMAN, SubType.WEREWOLF})
@ManaCost("3R")
@Printings({@Printings.Printed(ex = Expansion.DARK_ASCENSION, r = Rarity.RARE)})
@ColorIdentity({Color.RED})
@BackFace(TovolarsMagehunter.class)
public final class MondronenShaman extends Card
{
	public MondronenShaman(GameState state)
	{
		super(state);

		this.setPower(3);
		this.setToughness(2);

		// At the beginning of each upkeep, if no spells were cast last turn,
		// transform Mondronen Shaman.
		this.addAbility(new Werewolves.BecomeFuzzy(state, this.getName()));
	}
}
