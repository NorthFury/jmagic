package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.CantBlock;
import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.engine.*;

@Name("Nightshade Stinger")
@Types({Type.CREATURE})
@SubTypes({SubType.FAERIE, SubType.ROGUE})
@ManaCost("B")
@Printings({@Printings.Printed(ex = Expansion.LORWYN, r = Rarity.COMMON)})
@ColorIdentity({Color.BLACK})
public final class NightshadeStinger extends Card
{
	public NightshadeStinger(GameState state)
	{
		super(state);

		this.setPower(1);
		this.setToughness(1);

		// Flying
		this.addAbility(new Flying(state));

		// Nightshade Stinger can't block.
		this.addAbility(new CantBlock(state, this.getName()));
	}
}
