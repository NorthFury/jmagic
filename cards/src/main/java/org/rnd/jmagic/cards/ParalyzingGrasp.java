package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Enchant;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;
import org.rnd.jmagic.engine.patterns.*;

@Name("Paralyzing Grasp")
@Types({Type.ENCHANTMENT})
@SubTypes({SubType.AURA})
@ManaCost("2U")
@Printings({@Printings.Printed(ex = Expansion.RETURN_TO_RAVNICA, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.ZENDIKAR, r = Rarity.COMMON)})
@ColorIdentity({Color.BLUE})
public final class ParalyzingGrasp extends Card
{
	public static final class Paralyze extends StaticAbility
	{
		public Paralyze(GameState state)
		{
			super(state, "Enchanted creature doesn't untap during its controller's untap step.");

			// Enchanted creature doesn't untap during its controller's untap
			// step.
			EventPattern prohibitPattern = new UntapDuringControllersUntapStep(EnchantedBy.instance(This.instance()));

			ContinuousEffect.Part part = new ContinuousEffect.Part(ContinuousEffectType.PROHIBIT);
			part.parameters.put(ContinuousEffectType.Parameter.PROHIBITION, Identity.instance(prohibitPattern));
			this.addEffectPart(part);
		}
	}

	public ParalyzingGrasp(GameState state)
	{
		super(state);

		// Enchant creature
		this.addAbility(new Enchant.Creature(state));

		// Enchanted creature doesn't untap during its controller's untap step.
		this.addAbility(new Paralyze(state));
	}
}
