package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Landwalk;
import org.rnd.jmagic.engine.*;

@Name("Somberwald Dryad")
@Types({Type.CREATURE})
@SubTypes({SubType.DRYAD})
@ManaCost("1G")
@Printings({@Printings.Printed(ex = Expansion.DARK_ASCENSION, r = Rarity.COMMON)})
@ColorIdentity({Color.GREEN})
public final class SomberwaldDryad extends Card
{
	public SomberwaldDryad(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(2);

		// Forestwalk (This creature is unblockable as long as defending player
		// controls a Forest.)
		this.addAbility(new Landwalk.Forestwalk(state));
	}
}
