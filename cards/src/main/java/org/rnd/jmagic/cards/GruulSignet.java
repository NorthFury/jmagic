package org.rnd.jmagic.cards;

import org.rnd.jmagic.cardTemplates.Signet;
import org.rnd.jmagic.engine.*;

@Name("Gruul Signet")
@ManaCost("2")
@Types({Type.ARTIFACT})
@Printings({@Printings.Printed(ex = Expansion.COMMANDER, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.GUILDPACT, r = Rarity.COMMON)})
@ColorIdentity({Color.GREEN, Color.RED})
public final class GruulSignet extends Signet
{
	public GruulSignet(GameState state)
	{
		super(state, 'R', 'G');
	}
}
