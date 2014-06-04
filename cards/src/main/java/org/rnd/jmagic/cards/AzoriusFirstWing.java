package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.abilities.keywords.Protection;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Azorius First-Wing")
@Types({Type.CREATURE})
@SubTypes({SubType.GRIFFIN})
@ManaCost("WU")
@Printings({@Printings.Printed(ex = Expansion.DISSENSION, r = Rarity.COMMON)})
@ColorIdentity({Color.BLUE, Color.WHITE})
public final class AzoriusFirstWing extends Card
{
	public AzoriusFirstWing(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(2);

		// Flying, protection from enchantments
		this.addAbility(new Flying(state));
		this.addAbility(new Protection.From(state, HasType.instance(Type.ENCHANTMENT), "enchantments"));
	}
}
