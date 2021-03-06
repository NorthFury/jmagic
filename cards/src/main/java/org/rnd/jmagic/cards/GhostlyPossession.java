package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.PreventCombatDamageDealtToOrBy;
import org.rnd.jmagic.abilities.keywords.Enchant;
import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Ghostly Possession")
@Types({Type.ENCHANTMENT})
@SubTypes({SubType.AURA})
@ManaCost("2W")
@Printings({@Printings.Printed(ex = Expansion.INNISTRAD, r = Rarity.COMMON)})
@ColorIdentity({Color.WHITE})
public final class GhostlyPossession extends Card
{
	public static final class GhostlyPossessionAbility1 extends StaticAbility
	{
		public GhostlyPossessionAbility1(GameState state)
		{
			super(state, "Enchanted creature has flying.");
			this.addEffectPart(addAbilityToObject(EnchantedBy.instance(This.instance()), Flying.class));
		}
	}

	public static final class GhostlyPossessionAbility2 extends StaticAbility
	{
		public GhostlyPossessionAbility2(GameState state)
		{
			super(state, "Prevent all combat damage that would be dealt to and dealt by enchanted creature.");
			SetGenerator enchantedCreature = EnchantedBy.instance(This.instance());
			ReplacementEffect replacement = new PreventCombatDamageDealtToOrBy(state.game, enchantedCreature, "enchanted creature");
			this.addEffectPart(replacementEffectPart(replacement));
		}
	}

	public GhostlyPossession(GameState state)
	{
		super(state);

		// Enchant creature
		this.addAbility(new Enchant.Creature(state));

		// Enchanted creature has flying.
		this.addAbility(new GhostlyPossessionAbility1(state));

		// Prevent all combat damage that would be dealt to and dealt by
		// enchanted creature.
		this.addAbility(new GhostlyPossessionAbility2(state));
	}
}
