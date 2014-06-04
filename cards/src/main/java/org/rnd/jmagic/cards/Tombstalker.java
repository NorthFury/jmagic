package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Delve;
import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.engine.*;

@Name("Tombstalker")
@Types({Type.CREATURE})
@SubTypes({SubType.DEMON})
@ManaCost("6BB")
@Printings({@Printings.Printed(ex = Expansion.FUTURE_SIGHT, r = Rarity.RARE)})
@ColorIdentity({Color.BLACK})
public final class Tombstalker extends Card
{
	public Tombstalker(GameState state)
	{
		super(state);

		this.setPower(5);
		this.setToughness(5);

		// Flying
		this.addAbility(new Flying(state));

		// Delve (You may exile any number of cards from your graveyard as you
		// cast this spell. It costs (1) less to cast for each card exiled this
		// way.)
		this.addAbility(new Delve(state));
	}
}
