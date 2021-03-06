package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.engine.*;

@Name("Talrand's Invocation")
@Types({Type.SORCERY})
@ManaCost("2UU")
@Printings({@Printings.Printed(ex = Expansion.MAGIC_2013, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.BLUE})
public final class TalrandsInvocation extends Card
{
	public TalrandsInvocation(GameState state)
	{
		super(state);

		// Put two 2/2 blue Drake creature tokens with flying onto the
		// battlefield.
		CreateTokensFactory factory = new CreateTokensFactory(2, 2, 2, "Put two 2/2 blue Drake creature tokens with flying onto the battlefield.");
		factory.setColors(Color.BLUE);
		factory.setSubTypes(SubType.DRAKE);
		factory.addAbility(Flying.class);
		this.addEffect(factory.getEventFactory());
	}
}
