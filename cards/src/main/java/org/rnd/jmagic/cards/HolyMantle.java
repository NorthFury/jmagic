package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Enchant;
import org.rnd.jmagic.abilities.keywords.Protection;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Holy Mantle")
@Types({Type.ENCHANTMENT})
@SubTypes({SubType.AURA})
@ManaCost("2WW")
@Printings({@Printings.Printed(ex = Expansion.GATECRASH, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.WHITE})
public final class HolyMantle extends Card
{
	public static final class HolyMantleAbility1 extends StaticAbility
	{
		public HolyMantleAbility1(GameState state)
		{
			super(state, "Enchanted creature gets +2/+2 and has protection from creatures.");
			SetGenerator enchantedCreature = EnchantedBy.instance(This.instance());
			this.addEffectPart(modifyPowerAndToughness(enchantedCreature, +2, +2));
			this.addEffectPart(addAbilityToObject(enchantedCreature, Protection.FromCreatures.class));
		}
	}

	public HolyMantle(GameState state)
	{
		super(state);

		// Enchant creature
		this.addAbility(new Enchant.Creature(state));

		// Enchanted creature gets +2/+2 and has protection from creatures.
		this.addAbility(new HolyMantleAbility1(state));
	}
}
