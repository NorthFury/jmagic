package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.Werewolves;
import org.rnd.jmagic.abilities.keywords.Defender;
import org.rnd.jmagic.engine.*;

@Name("Hanweir Watchkeep")
@Types({Type.CREATURE})
@SubTypes({SubType.WARRIOR, SubType.HUMAN, SubType.WEREWOLF})
@ManaCost("2R")
@Printings({@Printings.Printed(ex = Expansion.INNISTRAD, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.RED})
@BackFace(BaneofHanweir.class)
public final class HanweirWatchkeep extends Card
{
	public HanweirWatchkeep(GameState state)
	{
		super(state);

		this.setPower(1);
		this.setToughness(5);

		// Defender
		this.addAbility(new Defender(state));

		// At the beginning of each upkeep, if no spells were cast last turn,
		// transform Hanweir Watchkeep.
		this.addAbility(new Werewolves.BecomeFuzzy(state, "Hanweir Watchkeep"));
	}
}
