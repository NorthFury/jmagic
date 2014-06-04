package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Enchant;
import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.abilities.keywords.Vigilance;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Serra's Embrace")
@Types({Type.ENCHANTMENT})
@SubTypes({SubType.AURA})
@ManaCost("2WW")
@Printings({@Printings.Printed(ex = Expansion.TENTH_EDITION, r = Rarity.UNCOMMON), @Printings.Printed(ex = Expansion.SEVENTH_EDITION, r = Rarity.UNCOMMON), @Printings.Printed(ex = Expansion.URZAS_SAGA, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.WHITE})
public final class SerrasEmbrace extends Card
{
	public static final class Effect extends StaticAbility
	{
		public Effect(GameState state)
		{
			super(state, "Enchanted creature gets +2/+2 and has flying and vigilance.");

			SetGenerator enchantedCreature = EnchantedBy.instance(This.instance());

			this.addEffectPart(modifyPowerAndToughness(enchantedCreature, 2, 2));
			this.addEffectPart(addAbilityToObject(enchantedCreature, Flying.class, Vigilance.class));
		}
	}

	public SerrasEmbrace(GameState state)
	{
		super(state);

		this.addAbility(new Enchant.Creature(state));
		this.addAbility(new Effect(state));
	}
}
