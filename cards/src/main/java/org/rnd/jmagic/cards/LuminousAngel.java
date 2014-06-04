package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.engine.*;

@Name("Luminous Angel")
@Types({Type.CREATURE})
@SubTypes({SubType.ANGEL})
@ManaCost("4WWW")
@Printings({@Printings.Printed(ex = Expansion.MIRRODIN, r = Rarity.RARE)})
@ColorIdentity({Color.WHITE})
public final class LuminousAngel extends Card
{
	public static final class SpiritBabies extends EventTriggeredAbility
	{
		public SpiritBabies(GameState state)
		{
			super(state, "At the beginning of your upkeep, you may put a 1/1 white Spirit creature token with flying onto the battlefield.");
			this.addPattern(atTheBeginningOfYourUpkeep());

			CreateTokensFactory token = new CreateTokensFactory(1, 1, 1, "Put a 1/1 white Spirit creature token with flying onto the battlefield");
			token.setColors(Color.WHITE);
			token.setSubTypes(SubType.SPIRIT);
			token.addAbility(Flying.class);
			this.addEffect(youMay(token.getEventFactory(), "You may put a 1/1 white Spirit creature token with flying onto the battlefield."));
		}
	}

	public LuminousAngel(GameState state)
	{
		super(state);

		this.setPower(4);
		this.setToughness(4);

		// Flying
		this.addAbility(new Flying(state));

		// At the beginning of your upkeep, you may put a 1/1 white Spirit
		// creature token with flying onto the battlefield.
		this.addAbility(new SpiritBabies(state));
	}
}
