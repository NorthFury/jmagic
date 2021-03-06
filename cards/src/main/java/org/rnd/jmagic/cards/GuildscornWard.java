package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Enchant;
import org.rnd.jmagic.abilities.keywords.Protection;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Guildscorn Ward")
@Types({Type.ENCHANTMENT})
@SubTypes({SubType.AURA})
@ManaCost("W")
@Printings({@Printings.Printed(ex = Expansion.GATECRASH, r = Rarity.COMMON)})
@ColorIdentity({Color.WHITE})
public final class GuildscornWard extends Card
{
	public static final class ProtectionFromMulticolored extends Protection
	{
		public ProtectionFromMulticolored(GameState state)
		{
			super(state, Multicolored.instance(), "multicolored");
		}
	}

	public static final class GuildscornWardAbility1 extends StaticAbility
	{
		public GuildscornWardAbility1(GameState state)
		{
			super(state, "Enchanted creature has protection from multicolored.");

			SetGenerator enchanted = EnchantedBy.instance(This.instance());
			this.addEffectPart(addAbilityToObject(enchanted, ProtectionFromMulticolored.class));
		}
	}

	public GuildscornWard(GameState state)
	{
		super(state);

		// Enchant creature
		this.addAbility(new Enchant.Creature(state));

		// Enchanted creature has protection from multicolored.
		this.addAbility(new GuildscornWardAbility1(state));
	}
}
