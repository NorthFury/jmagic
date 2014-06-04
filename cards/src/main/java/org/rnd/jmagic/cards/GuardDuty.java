package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Defender;
import org.rnd.jmagic.abilities.keywords.Enchant;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Guard Duty")
@Types({Type.ENCHANTMENT})
@SubTypes({SubType.AURA})
@ManaCost("W")
@Printings({@Printings.Printed(ex = Expansion.RISE_OF_THE_ELDRAZI, r = Rarity.COMMON)})
@ColorIdentity({Color.WHITE})
public final class GuardDuty extends Card
{
	public static final class GrantDefender extends StaticAbility
	{
		public GrantDefender(GameState state)
		{
			super(state, "Enchanted creature has defender.");

			this.addEffectPart(addAbilityToObject(EnchantedBy.instance(This.instance()), Defender.class));
		}
	}

	public GuardDuty(GameState state)
	{
		super(state);

		// Enchant creature
		this.addAbility(new Enchant.Creature(state));

		// Enchanted creature has defender.
		this.addAbility(new GrantDefender(state));
	}
}
