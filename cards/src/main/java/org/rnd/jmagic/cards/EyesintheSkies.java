package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.engine.*;

@Name("Eyes in the Skies")
@Types({Type.INSTANT})
@ManaCost("3W")
@Printings({@Printings.Printed(ex = Expansion.RETURN_TO_RAVNICA, r = Rarity.COMMON)})
@ColorIdentity({Color.WHITE})
public final class EyesintheSkies extends Card
{
	public EyesintheSkies(GameState state)
	{
		super(state);

		// Put a 1/1 white Bird creature token with flying onto the battlefield,
		CreateTokensFactory factory = new CreateTokensFactory(1, 1, 1, "Put a 1/1 white Bird creature token with flying onto the battlefield,");
		factory.setColors(Color.WHITE);
		factory.setSubTypes(SubType.BIRD);
		factory.addAbility(Flying.class);
		this.addEffect(factory.getEventFactory());

		// then populate.
		this.addEffect(populate("then populate."));
	}
}
