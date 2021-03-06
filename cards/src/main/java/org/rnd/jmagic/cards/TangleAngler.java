package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Infect;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Tangle Angler")
@Types({Type.CREATURE})
@SubTypes({SubType.HORROR})
@ManaCost("3G")
@Printings({@Printings.Printed(ex = Expansion.SCARS_OF_MIRRODIN, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.GREEN})
public final class TangleAngler extends Card
{
	public static final class TangleAnglerAbility1 extends ActivatedAbility
	{
		public TangleAnglerAbility1(GameState state)
		{
			super(state, "(G): Target creature blocks Tangle Angler this turn if able.");
			this.setManaCost(new ManaPool("(G)"));

			SetGenerator target = targetedBy(this.addTarget(CreaturePermanents.instance(), "target creature"));

			ContinuousEffect.Part part = new ContinuousEffect.Part(ContinuousEffectType.BLOCKING_REQUIREMENT);
			part.parameters.put(ContinuousEffectType.Parameter.ATTACKING, ABILITY_SOURCE_OF_THIS);
			part.parameters.put(ContinuousEffectType.Parameter.DEFENDING, target);
		}
	}

	public TangleAngler(GameState state)
	{
		super(state);

		this.setPower(1);
		this.setToughness(5);

		// Infect (This creature deals damage to creatures in the form of -1/-1
		// counters and to players in the form of poison counters.)
		this.addAbility(new Infect(state));

		// (G): Target creature blocks Tangle Angler this turn if able.
		this.addAbility(new TangleAnglerAbility1(state));
	}
}
