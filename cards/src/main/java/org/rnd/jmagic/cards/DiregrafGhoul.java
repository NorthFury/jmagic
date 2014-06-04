package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.EntersTheBattlefieldTapped;
import org.rnd.jmagic.engine.*;

@Name("Diregraf Ghoul")
@Types({Type.CREATURE})
@SubTypes({SubType.ZOMBIE})
@ManaCost("B")
@Printings({@Printings.Printed(ex = Expansion.INNISTRAD, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.BLACK})
public final class DiregrafGhoul extends Card
{
	public DiregrafGhoul(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(2);

		// Diregraf Ghoul enters the battlefield tapped.
		this.addAbility(new EntersTheBattlefieldTapped(state, this.getName()));
	}
}
