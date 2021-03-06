package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.engine.*;

@Name("Geyser Glider")
@Types({Type.CREATURE})
@SubTypes({SubType.ELEMENTAL, SubType.BEAST})
@ManaCost("3RR")
@Printings({@Printings.Printed(ex = Expansion.ZENDIKAR, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.RED})
public final class GeyserGlider extends Card
{
	public static final class BirdyBoiler extends EventTriggeredAbility
	{
		public BirdyBoiler(GameState state)
		{
			super(state, "Whenever a land enters the battlefield under your control, Geyser Glider gains flying until end of turn.");

			this.addPattern(landfall());

			this.addEffect(addAbilityUntilEndOfTurn(ABILITY_SOURCE_OF_THIS, Flying.class, "Geyser Glider gains flying until end of turn"));
		}
	}

	public GeyserGlider(GameState state)
	{
		super(state);

		this.setPower(4);
		this.setToughness(4);

		// Landfall \u2014 Whenever a land enters the battlefield under your
		// control, Geyser Glider gains flying until end of turn.
		this.addAbility(new BirdyBoiler(state));
	}
}
