package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.Ping;
import org.rnd.jmagic.abilities.keywords.Enchant;
import org.rnd.jmagic.abilities.keywords.Haste;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Lightning Prowess")
@Types({Type.ENCHANTMENT})
@SubTypes({SubType.AURA})
@ManaCost("2R")
@Printings({@Printings.Printed(ex = Expansion.AVACYN_RESTORED, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.RED})
public final class LightningProwess extends Card
{
	public static final class LightningProwessAbility1 extends StaticAbility
	{
		public LightningProwessAbility1(GameState state)
		{
			super(state, "Enchanted creature has haste and \"(T): This creature deals 1 damage to target creature or player.\"");
			this.addEffectPart(addAbilityToObject(EnchantedBy.instance(This.instance()), Haste.class, Ping.class));
		}
	}

	public LightningProwess(GameState state)
	{
		super(state);

		// Enchant creature
		this.addAbility(new Enchant.Creature(state));

		// Enchanted creature has haste and
		// "(T): This creature deals 1 damage to target creature or player."
		this.addAbility(new LightningProwessAbility1(state));
	}
}
