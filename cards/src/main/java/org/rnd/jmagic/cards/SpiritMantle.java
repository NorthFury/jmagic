package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Enchant;
import org.rnd.jmagic.abilities.keywords.Protection;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Spirit Mantle")
@Types({Type.ENCHANTMENT})
@SubTypes({SubType.AURA})
@ManaCost("1W")
@Printings({@Printings.Printed(ex = Expansion.MAGIC_2012, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.WHITE})
public final class SpiritMantle extends Card
{
	public static final class SpiritMantleAbility1 extends StaticAbility
	{
		public SpiritMantleAbility1(GameState state)
		{
			super(state, "Enchanted creature gets +1/+1 and has protection from creatures.");
			this.addEffectPart(modifyPowerAndToughness(EnchantedBy.instance(This.instance()), +1, +1));
			this.addEffectPart(addAbilityToObject(EnchantedBy.instance(This.instance()), Protection.FromCreatures.class));
		}
	}

	public SpiritMantle(GameState state)
	{
		super(state);

		// Enchant creature
		this.addAbility(new Enchant.Creature(state));

		// Enchanted creature gets +1/+1 and has protection from creatures. (It
		// can't be blocked, targeted, or dealt damage by creatures.)
		this.addAbility(new SpiritMantleAbility1(state));
	}
}
