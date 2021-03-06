package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.Werewolves;
import org.rnd.jmagic.engine.*;

@Name("Hinterland Hermit")
@Types({Type.CREATURE})
@SubTypes({SubType.HUMAN, SubType.WEREWOLF})
@ManaCost("1R")
@Printings({@Printings.Printed(ex = Expansion.DARK_ASCENSION, r = Rarity.COMMON)})
@ColorIdentity({Color.RED})
@BackFace(HinterlandScourge.class)
public final class HinterlandHermit extends Card
{
	public HinterlandHermit(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(1);

		// At the beginning of each upkeep, if no spells were cast last turn,
		// transform Hinterland Hermit.
		this.addAbility(new Werewolves.BecomeFuzzy(state, this.getName()));
	}
}
