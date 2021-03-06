package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Enchant;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Fear")
@Types({Type.ENCHANTMENT})
@SubTypes({SubType.AURA})
@ManaCost("BB")
@Printings({@Printings.Printed(ex = Expansion.TENTH_EDITION, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.NINTH_EDITION, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.EIGHTH_EDITION, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.SEVENTH_EDITION, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.SIXTH_EDITION, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.FIFTH_EDITION, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.ICE_AGE, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.FOURTH_EDITION, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.REVISED, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.UNLIMITED, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.BETA, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.ALPHA, r = Rarity.COMMON)})
@ColorIdentity({Color.BLACK})
public final class Fear extends Card
{
	public static final class FearAbility0 extends StaticAbility
	{
		public FearAbility0(GameState state)
		{
			super(state, "Enchanted creature has fear.");
			this.addEffectPart(addAbilityToObject(EnchantedBy.instance(This.instance()), org.rnd.jmagic.abilities.keywords.Fear.class));
		}
	}

	public Fear(GameState state)
	{
		super(state);

		this.addAbility(new Enchant.Creature(state));
		this.addAbility(new FearAbility0(state));
	}
}
