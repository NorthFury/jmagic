package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.CantBlock;
import org.rnd.jmagic.engine.*;

@Name("Unhallowed Cathar")
@Types({Type.CREATURE})
@SubTypes({SubType.ZOMBIE, SubType.SOLDIER})
@Printings({@Printings.Printed(ex = Expansion.DARK_ASCENSION, r = Rarity.COMMON)})
@ColorIdentity({Color.BLACK})
public final class UnhallowedCathar extends AlternateCard
{
	public UnhallowedCathar(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(1);

		this.setColorIndicator(Color.BLACK);

		// Unhallowed Cathar can't block.
		this.addAbility(new CantBlock(state, this.getName()));
	}
}
