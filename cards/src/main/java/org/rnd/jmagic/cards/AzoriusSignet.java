package org.rnd.jmagic.cards;

import org.rnd.jmagic.cardTemplates.Signet;
import org.rnd.jmagic.engine.*;

@Name("Azorius Signet")
@ManaCost("2")
@Types({Type.ARTIFACT})
@Printings({@Printings.Printed(ex = Expansion.DISSENSION, r = Rarity.COMMON)})
@ColorIdentity({Color.BLUE, Color.WHITE})
public final class AzoriusSignet extends Signet
{
	public AzoriusSignet(GameState state)
	{
		super(state, 'W', 'U');
	}
}
