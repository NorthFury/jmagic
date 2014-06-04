package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.abilities.keywords.Haste;
import org.rnd.jmagic.engine.*;

@Name("Volcanic Dragon")
@Types({Type.CREATURE})
@SubTypes({SubType.DRAGON})
@ManaCost("4RR")
@Printings({@Printings.Printed(ex = Expansion.MAGIC_2012, r = Rarity.UNCOMMON), @Printings.Printed(ex = Expansion.STARTER, r = Rarity.RARE), @Printings.Printed(ex = Expansion.SIXTH_EDITION, r = Rarity.RARE), @Printings.Printed(ex = Expansion.PORTAL, r = Rarity.RARE), @Printings.Printed(ex = Expansion.MIRAGE, r = Rarity.RARE)})
@ColorIdentity({Color.RED})
public final class VolcanicDragon extends Card
{
	public VolcanicDragon(GameState state)
	{
		super(state);

		this.setPower(4);
		this.setToughness(4);

		// Flying
		this.addAbility(new Flying(state));

		// Haste (This creature can attack and (T) as soon as it comes under
		// your control.)
		this.addAbility(new Haste(state));
	}
}
