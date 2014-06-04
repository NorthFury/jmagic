package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.EntersTheBattlefieldTapped;
import org.rnd.jmagic.abilities.TapForG;
import org.rnd.jmagic.abilities.keywords.Graft;
import org.rnd.jmagic.engine.*;

@Name("Llanowar Reborn")
@Types({Type.LAND})
@Printings({@Printings.Printed(ex = Expansion.FUTURE_SIGHT, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.GREEN})
public final class LlanowarReborn extends Card
{
	public LlanowarReborn(GameState state)
	{
		super(state);

		this.addAbility(new EntersTheBattlefieldTapped(state, this.getName()));
		this.addAbility(new TapForG(state));
		this.addAbility(new Graft(state, 1));
	}
}
