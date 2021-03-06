package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.engine.*;

@Name("Midnight Haunting")
@Types({Type.INSTANT})
@ManaCost("2W")
@Printings({@Printings.Printed(ex = Expansion.INNISTRAD, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.WHITE})
public final class MidnightHaunting extends Card
{
	public MidnightHaunting(GameState state)
	{
		super(state);

		// Put two 1/1 white Spirit creature tokens with flying onto the
		// battlefield.
		CreateTokensFactory factory = new CreateTokensFactory(2, 1, 1, "Put two 1/1 white Spirit creature tokens with flying onto the battlefield.");
		factory.setColors(Color.WHITE);
		factory.setSubTypes(SubType.SPIRIT);
		factory.addAbility(Flying.class);
		this.addEffect(factory.getEventFactory());
	}
}
