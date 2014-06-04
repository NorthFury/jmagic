package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.abilities.keywords.Kicker;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Tempest Owl")
@Types({Type.CREATURE})
@SubTypes({SubType.BIRD})
@ManaCost("1U")
@Printings({@Printings.Printed(ex = Expansion.ZENDIKAR, r = Rarity.COMMON)})
@ColorIdentity({Color.BLUE})
public final class TempestOwl extends Card
{
	public static final class OwlTap extends EventTriggeredAbility
	{
		private final CostCollection kickerCost;

		public OwlTap(GameState state, CostCollection kickerCost)
		{
			super(state, "When Tempest Owl enters the battlefield, if it was kicked, tap up to three target permanents.");
			this.kickerCost = kickerCost;

			this.addPattern(whenThisEntersTheBattlefield());
			this.interveningIf = ThisPermanentWasKicked.instance(kickerCost);

			Target target = this.addTarget(Permanents.instance(), "up to three target permanents");
			target.setNumber(0, 3);

			this.addEffect(tap(targetedBy(target), "Tap up to three target permanents."));
		}

		@Override
		public OwlTap create(Game game)
		{
			return new OwlTap(game.physicalState, this.kickerCost);
		}
	}

	public TempestOwl(GameState state)
	{
		super(state);

		this.setPower(1);
		this.setToughness(2);

		// Kicker (4)(U) (You may pay an additional (4)(U) as you cast this
		// spell.)
		Kicker ability = new Kicker(state, "(4)(U)");
		this.addAbility(ability);
		CostCollection kickerCost = ability.costCollections[0];

		// Flying
		this.addAbility(new Flying(state));

		// When Tempest Owl enters the battlefield, if it was kicked, tap up to
		// three target permanents.
		this.addAbility(new OwlTap(state, kickerCost));
	}
}
