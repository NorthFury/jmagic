package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;
import org.rnd.jmagic.engine.*;

@Name("Serum Visions")
@Types({Type.SORCERY})
@ManaCost("U")
@Printings({@Printings.Printed(ex = Expansion.FIFTH_DAWN, r = Rarity.COMMON)})
@ColorIdentity({Color.BLUE})
public final class SerumVisions extends Card
{
	public SerumVisions(GameState state)
	{
		super(state);

		this.addEffect(drawACard());
		this.addEffect(scry(2, "\n\nScry 2."));
	}
}
