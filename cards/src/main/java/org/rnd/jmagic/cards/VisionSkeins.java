package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.Players;

@Name("Vision Skeins")
@Types({Type.INSTANT})
@ManaCost("1U")
@Printings({@Printings.Printed(ex = Expansion.COMMANDER, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.DISSENSION, r = Rarity.COMMON)})
@ColorIdentity({Color.BLUE})
public final class VisionSkeins extends Card
{
	public VisionSkeins(GameState state)
	{
		super(state);

		this.addEffect(drawCards(Players.instance(), 2, "Each player draws two cards."));
	}
}
