package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.abilities.keywords.Vanishing;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Aven Riftwatcher")
@Types({Type.CREATURE})
@SubTypes({SubType.REBEL, SubType.SOLDIER, SubType.BIRD})
@ManaCost("2W")
@Printings({@Printings.Printed(ex = Expansion.PLANAR_CHAOS, r = Rarity.COMMON)})
@ColorIdentity({Color.WHITE})
public final class AvenRiftwatcher extends Card
{
	public static final class RiftTube extends EventTriggeredAbility
	{
		public RiftTube(GameState state)
		{
			super(state, "When Aven Riftwatcher enters the battlefield or leaves the battlefield, you gain 2 life.");

			this.addPattern(whenThisEntersTheBattlefield());
			this.addPattern(whenThisLeavesTheBattlefield());

			this.addEffect(gainLife(You.instance(), 2, "You gain 2 life."));
		}
	}

	public AvenRiftwatcher(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(3);

		// Flying
		this.addAbility(new Flying(state));

		// Vanishing 3
		this.addAbility(new Vanishing(state, 3));

		// When Aven Riftwatcher enters the battlefield or leaves the
		// battlefield, you gain 2 life.
		this.addAbility(new RiftTube(state));
	}
}
