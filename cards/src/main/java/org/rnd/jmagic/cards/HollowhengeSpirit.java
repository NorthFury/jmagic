package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Flash;
import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Hollowhenge Spirit")
@Types({Type.CREATURE})
@SubTypes({SubType.SPIRIT})
@ManaCost("3W")
@Printings({@Printings.Printed(ex = Expansion.DARK_ASCENSION, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.WHITE})
public final class HollowhengeSpirit extends Card
{
	public static final class HollowhengeSpiritAbility2 extends EventTriggeredAbility
	{
		public HollowhengeSpiritAbility2(GameState state)
		{
			super(state, "When Hollowhenge Spirit enters the battlefield, remove target attacking or blocking creature from combat.");
			this.addPattern(whenThisEntersTheBattlefield());

			SetGenerator creatures = Union.instance(Attacking.instance(), Blocking.instance());
			SetGenerator target = targetedBy(this.addTarget(creatures, "target attacking or blocking creature"));
			EventFactory remove = new EventFactory(EventType.REMOVE_FROM_COMBAT, "Remove target attacking or blocking creature from combat");
			remove.parameters.put(EventType.Parameter.OBJECT, target);
			this.addEffect(remove);
		}
	}

	public HollowhengeSpirit(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(2);

		// Flash (You may cast this spell any time you could cast an instant.)
		this.addAbility(new Flash(state));

		// Flying
		this.addAbility(new Flying(state));

		// When Hollowhenge Spirit enters the battlefield, remove target
		// attacking or blocking creature from combat.
		this.addAbility(new HollowhengeSpiritAbility2(state));
	}
}
