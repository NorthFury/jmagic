package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.cardTemplates.*;
import org.rnd.jmagic.engine.*;

@Name("Esper Stormblade")
@ManaCost("(WB)U")
@Types({Type.ARTIFACT, Type.CREATURE})
@SubTypes({SubType.WIZARD, SubType.VEDALKEN})
@Printings({@Printings.Printed(ex = Expansion.ALARA_REBORN, r = Rarity.COMMON)})
@ColorIdentity({Color.BLUE, Color.WHITE, Color.BLACK})
public final class EsperStormblade extends AlaraRebornBlade
{
	public EsperStormblade(GameState state)
	{
		super(state, Flying.class);
	}
}
