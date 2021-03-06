package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.Werewolves;
import org.rnd.jmagic.abilities.keywords.FirstStrike;
import org.rnd.jmagic.engine.*;

@Name("Kruin Outlaw")
@Types({Type.CREATURE})
@SubTypes({SubType.HUMAN, SubType.ROGUE, SubType.WEREWOLF})
@ManaCost("1RR")
@Printings({@Printings.Printed(ex = Expansion.INNISTRAD, r = Rarity.RARE)})
@ColorIdentity({Color.RED})
@BackFace(TerrorofKruinPass.class)
public final class KruinOutlaw extends Card
{
	public KruinOutlaw(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(2);

		// First strike
		this.addAbility(new FirstStrike(state));

		// At the beginning of each upkeep, if no spells were cast last turn,
		// transform Kruin Outlaw.
		this.addAbility(new Werewolves.BecomeFuzzy(state, this.getName()));
	}
}
