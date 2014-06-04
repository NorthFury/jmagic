package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Enchant;
import org.rnd.jmagic.abilities.keywords.Shroud;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Robe of Mirrors")
@Types({Type.ENCHANTMENT})
@SubTypes({SubType.AURA})
@ManaCost("U")
@Printings({@Printings.Printed(ex = Expansion.TENTH_EDITION, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.EXODUS, r = Rarity.COMMON)})
@ColorIdentity({Color.BLUE})
public final class RobeofMirrors extends Card
{
	public static final class Mirror extends StaticAbility
	{
		public Mirror(GameState state)
		{
			super(state, "Enchanted creature has shroud.");

			SetGenerator enchantedCreature = EnchantedBy.instance(This.instance());

			this.addEffectPart(addAbilityToObject(enchantedCreature, Shroud.class));
		}
	}

	public RobeofMirrors(GameState state)
	{
		super(state);

		this.addAbility(new Enchant.Creature(state));
		this.addAbility(new Mirror(state));
	}
}
