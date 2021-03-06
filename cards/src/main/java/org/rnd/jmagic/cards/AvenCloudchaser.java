package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Aven Cloudchaser")
@Types({Type.CREATURE})
@SubTypes({SubType.SOLDIER, SubType.BIRD})
@ManaCost("3W")
@Printings({@Printings.Printed(ex = Expansion.TENTH_EDITION, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.NINTH_EDITION, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.EIGHTH_EDITION, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.ODYSSEY, r = Rarity.COMMON)})
@ColorIdentity({Color.WHITE})
public final class AvenCloudchaser extends Card
{
	public static final class AntiEnchantment extends EventTriggeredAbility
	{
		public AntiEnchantment(GameState state)
		{
			super(state, "When Aven Cloudchaser enters the battlefield, destroy target enchantment.");
			this.addPattern(whenThisEntersTheBattlefield());

			Target target = this.addTarget(EnchantmentPermanents.instance(), "target enchantment");
			this.addEffect(destroy(targetedBy(target), "Destroy target enchantment."));
		}
	}

	public AvenCloudchaser(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(2);

		this.addAbility(new Flying(state));
		this.addAbility(new AntiEnchantment(state));
	}
}
