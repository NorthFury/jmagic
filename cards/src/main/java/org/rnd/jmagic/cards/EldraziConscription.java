package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Annihilator;
import org.rnd.jmagic.abilities.keywords.Enchant;
import org.rnd.jmagic.abilities.keywords.Trample;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Eldrazi Conscription")
@Types({Type.ENCHANTMENT, Type.TRIBAL})
@SubTypes({SubType.ELDRAZI, SubType.AURA})
@ManaCost("8")
@Printings({@Printings.Printed(ex = Expansion.RISE_OF_THE_ELDRAZI, r = Rarity.RARE)})
@ColorIdentity({})
public final class EldraziConscription extends Card
{
	@Name("Annihilator 2")
	public static final class Annihilator2 extends Annihilator
	{
		public Annihilator2(GameState state)
		{
			super(state, 2);
		}
	}

	public static final class EnchantedCreatureIsAwesome extends StaticAbility
	{
		public EnchantedCreatureIsAwesome(GameState state)
		{
			super(state, "Enchanted creature gets +10/+10 and has trample and annihilator 2.");
			SetGenerator enchantedCreature = EnchantedBy.instance(This.instance());
			this.addEffectPart(modifyPowerAndToughness(enchantedCreature, +10, +10));
			this.addEffectPart(addAbilityToObject(enchantedCreature, Trample.class, Annihilator2.class));
		}
	}

	public EldraziConscription(GameState state)
	{
		super(state);

		// Enchant creature
		this.addAbility(new Enchant.Creature(state));

		// Enchanted creature gets +10/+10 and has trample and annihilator 2.
		this.addAbility(new EnchantedCreatureIsAwesome(state));
	}
}
