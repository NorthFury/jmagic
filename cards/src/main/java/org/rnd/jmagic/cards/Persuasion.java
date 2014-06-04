package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.YouControlEnchantedCreature;
import org.rnd.jmagic.abilities.keywords.Enchant;
import org.rnd.jmagic.engine.*;

@Name("Persuasion")
@Types({Type.ENCHANTMENT})
@SubTypes({SubType.AURA})
@ManaCost("3UU")
@Printings({@Printings.Printed(ex = Expansion.TENTH_EDITION, r = Rarity.UNCOMMON), @Printings.Printed(ex = Expansion.ODYSSEY, r = Rarity.RARE)})
@ColorIdentity({Color.BLUE})
public final class Persuasion extends Card
{
	public Persuasion(GameState state)
	{
		super(state);

		this.addAbility(new Enchant.Creature(state));
		this.addAbility(new YouControlEnchantedCreature(state));
	}
}
