package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.*;
import org.rnd.jmagic.abilities.keywords.Haste;
import org.rnd.jmagic.engine.*;

@Name("Crypt Ripper")
@Types({Type.CREATURE})
@SubTypes({SubType.SHADE})
@ManaCost("2BB")
@Printings({@Printings.Printed(ex = Expansion.ZENDIKAR, r = Rarity.COMMON)})
@ColorIdentity({Color.BLACK})
public final class CryptRipper extends Card
{
	public CryptRipper(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(2);

		this.addAbility(new Haste(state));

		this.addAbility(new ShadePump(state, "Crypt Ripper"));
	}
}
