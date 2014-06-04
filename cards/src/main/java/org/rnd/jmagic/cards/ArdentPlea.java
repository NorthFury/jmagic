package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Cascade;
import org.rnd.jmagic.abilities.keywords.Exalted;
import org.rnd.jmagic.engine.*;

@Name("Ardent Plea")
@Types({Type.ENCHANTMENT})
@ManaCost("1WU")
@Printings({@Printings.Printed(ex = Expansion.ALARA_REBORN, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.BLUE, Color.WHITE})
public final class ArdentPlea extends Card
{
	public ArdentPlea(GameState state)
	{
		super(state);

		this.addAbility(new Exalted(state));

		this.addAbility(new Cascade(state));
	}
}
