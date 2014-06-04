package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.FirstStrike;
import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.engine.*;

@Name("Peregrine Griffin")
@Types({Type.CREATURE})
@SubTypes({SubType.GRIFFIN})
@ManaCost("4W")
@Printings({@Printings.Printed(ex = Expansion.MAGIC_2012, r = Rarity.COMMON)})
@ColorIdentity({Color.WHITE})
public final class PeregrineGriffin extends Card
{
	public PeregrineGriffin(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(4);

		// Flying
		this.addAbility(new Flying(state));

		// First strike (This creature deals combat damage before creatures
		// without first strike.)
		this.addAbility(new FirstStrike(state));
	}
}
