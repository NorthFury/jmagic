package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Haste;
import org.rnd.jmagic.engine.*;

@Name("Viashino Sandscout")
@Types({Type.CREATURE})
@SubTypes({SubType.VIASHINO, SubType.SCOUT})
@ManaCost("1R")
@Printings({@Printings.Printed(ex = Expansion.TENTH_EDITION, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.URZAS_LEGACY, r = Rarity.COMMON)})
@ColorIdentity({Color.RED})
public final class ViashinoSandscout extends Card
{
	public static final class BounceEOT extends EventTriggeredAbility
	{
		public BounceEOT(GameState state)
		{
			super(state, "At the beginning of the end step, return Viashino Sandscout to its owner's hand.");
			this.addPattern(atTheBeginningOfTheEndStep());

			this.addEffect(bounce(ABILITY_SOURCE_OF_THIS, "Return Viashino Sandscout to its owner's hand."));
		}
	}

	public ViashinoSandscout(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(1);

		this.addAbility(new Haste(state));
		this.addAbility(new BounceEOT(state));
	}
}
