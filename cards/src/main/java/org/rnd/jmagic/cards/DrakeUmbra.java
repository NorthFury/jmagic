package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Enchant;
import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.abilities.keywords.TotemArmor;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Drake Umbra")
@Types({Type.ENCHANTMENT})
@SubTypes({SubType.AURA})
@ManaCost("4U")
@Printings({@Printings.Printed(ex = Expansion.RISE_OF_THE_ELDRAZI, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.BLUE})
public final class DrakeUmbra extends Card
{
	public static final class DrakePump extends StaticAbility
	{
		public DrakePump(GameState state)
		{
			super(state, "Enchanted creature gets +3/+3 and has flying.");

			SetGenerator enchantedCreature = EnchantedBy.instance(This.instance());

			this.addEffectPart(modifyPowerAndToughness(enchantedCreature, +3, +3));

			this.addEffectPart(addAbilityToObject(enchantedCreature, Flying.class));
		}
	}

	public DrakeUmbra(GameState state)
	{
		super(state);

		// Enchant creature
		this.addAbility(new Enchant.Creature(state));

		// Enchanted creature gets +3/+3 and has flying.
		this.addAbility(new DrakePump(state));

		// Totem armor
		this.addAbility(new TotemArmor(state));
	}
}
