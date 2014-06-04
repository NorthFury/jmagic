package org.rnd.jmagic.cards;

import org.rnd.jmagic.cardTemplates.Medallion;
import org.rnd.jmagic.engine.*;

@Name("Emerald Medallion")
@ManaCost("2")
@Types({Type.ARTIFACT})
@Printings({@Printings.Printed(ex = Expansion.TEMPEST, r = Rarity.RARE)})
@ColorIdentity({})
public final class EmeraldMedallion extends Medallion
{
	public EmeraldMedallion(GameState state)
	{
		super(state, Color.GREEN);
	}
}
