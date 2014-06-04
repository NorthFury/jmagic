package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Enchant;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Indestructibility")
@Types({Type.ENCHANTMENT})
@SubTypes({SubType.AURA})
@ManaCost("3W")
@Printings({@Printings.Printed(ex = Expansion.MAGIC_2010, r = Rarity.RARE)})
@ColorIdentity({Color.WHITE})
public final class Indestructibility extends Card
{
	public static final class EnchantedPermanentIsIndestructible extends StaticAbility
	{
		public EnchantedPermanentIsIndestructible(GameState state)
		{
			super(state, "Enchanted permanent is indestructible.");

			this.addEffectPart(indestructible(EnchantedBy.instance(This.instance())));
		}
	}

	public Indestructibility(GameState state)
	{
		super(state);

		// Enchant permanent
		this.addAbility(new Enchant.Permanent(state));

		// Enchanted permanent is indestructible. (Effects that say "destroy"
		// don't destroy that permanent. An indestructible creature can't be
		// destroyed by damage.)
		this.addAbility(new EnchantedPermanentIsIndestructible(state));
	}
}
