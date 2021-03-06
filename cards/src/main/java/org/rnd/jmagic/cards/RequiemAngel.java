package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Requiem Angel")
@Types({Type.CREATURE})
@SubTypes({SubType.ANGEL})
@ManaCost("5W")
@Printings({@Printings.Printed(ex = Expansion.DARK_ASCENSION, r = Rarity.RARE)})
@ColorIdentity({Color.WHITE})
public final class RequiemAngel extends Card
{
	public static final class RequiemAngelAbility1 extends EventTriggeredAbility
	{
		public RequiemAngelAbility1(GameState state)
		{
			super(state, "Whenever another non-Spirit creature you control dies, put a 1/1 white Spirit creature token with flying onto the battlefield.");

			SetGenerator affected = RelativeComplement.instance(CREATURES_YOU_CONTROL, Union.instance(ABILITY_SOURCE_OF_THIS, HasSubType.instance(SubType.SPIRIT)));
			this.addPattern(whenXDies(affected));

			CreateTokensFactory factory = new CreateTokensFactory(1, 1, 1, "Put a 1/1 white Spirit creature token with flying onto the battlefield.");
			factory.setColors(Color.WHITE);
			factory.setSubTypes(SubType.SPIRIT);
			factory.addAbility(Flying.class);
			this.addEffect(factory.getEventFactory());
		}
	}

	public RequiemAngel(GameState state)
	{
		super(state);

		this.setPower(5);
		this.setToughness(5);

		// Flying
		this.addAbility(new Flying(state));

		// Whenever another non-Spirit creature you control dies, put a 1/1
		// white Spirit creature token with flying onto the battlefield.
		this.addAbility(new RequiemAngelAbility1(state));
	}
}
