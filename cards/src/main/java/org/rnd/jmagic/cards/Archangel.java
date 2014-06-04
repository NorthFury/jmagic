package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.abilities.keywords.Vigilance;
import org.rnd.jmagic.engine.*;

@Name("Archangel")
@Types({Type.CREATURE})
@SubTypes({SubType.ANGEL})
@ManaCost("5WW")
@Printings({@Printings.Printed(ex = Expansion.AVACYN_RESTORED, r = Rarity.UNCOMMON), @Printings.Printed(ex = Expansion.STARTER, r = Rarity.RARE), @Printings.Printed(ex = Expansion.SIXTH_EDITION, r = Rarity.RARE), @Printings.Printed(ex = Expansion.PORTAL_SECOND_AGE, r = Rarity.RARE), @Printings.Printed(ex = Expansion.PORTAL, r = Rarity.RARE), @Printings.Printed(ex = Expansion.VISIONS, r = Rarity.RARE)})
@ColorIdentity({Color.WHITE})
public final class Archangel extends Card
{
	public Archangel(GameState state)
	{
		super(state);

		this.setPower(5);
		this.setToughness(5);

		// Flying, vigilance
		this.addAbility(new Flying(state));
		this.addAbility(new Vigilance(state));
	}
}
