package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.StaticPTChange;
import org.rnd.jmagic.abilities.keywords.Enchant;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Oakenform")
@Types({Type.ENCHANTMENT})
@SubTypes({SubType.AURA})
@ManaCost("2G")
@Printings({@Printings.Printed(ex = Expansion.MAGIC_2010, r = Rarity.COMMON)})
@ColorIdentity({Color.GREEN})
public final class Oakenform extends Card
{
	public Oakenform(GameState state)
	{
		super(state);

		this.addAbility(new Enchant.Creature(state));

		SetGenerator enchantedCreature = EnchantedBy.instance(This.instance());
		this.addAbility(new StaticPTChange(state, enchantedCreature, "Enchanted creature", +3, +3, false));
	}
}
