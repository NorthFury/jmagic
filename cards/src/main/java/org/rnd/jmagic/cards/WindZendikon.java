package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.EnchantedCardComesBackToHand;
import org.rnd.jmagic.abilities.StaticAnimation;
import org.rnd.jmagic.abilities.keywords.Enchant;
import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Wind Zendikon")
@Types({Type.ENCHANTMENT})
@SubTypes({SubType.AURA})
@ManaCost("U")
@Printings({@Printings.Printed(ex = Expansion.WORLDWAKE, r = Rarity.COMMON)})
@ColorIdentity({Color.BLUE})
public final class WindZendikon extends Card
{
	public WindZendikon(GameState state)
	{
		super(state);

		// Enchant land
		this.addAbility(new Enchant.Land(state));

		// Enchanted land is a 2/2 blue Elemental creature with flying. It's
		// still a land.
		Animator animator = new Animator(EnchantedBy.instance(This.instance()), 2, 2);
		animator.addColor(Color.BLUE);
		animator.addSubType(SubType.ELEMENTAL);
		animator.addAbility(Flying.class);
		this.addAbility(new StaticAnimation(state, animator, "Enchanted land is a 2/2 blue Elemental creature with flying. It's still a land."));

		// When enchanted land is put into a graveyard, return that card to its
		// owner's hand.
		this.addAbility(new EnchantedCardComesBackToHand(state, "enchanted land"));
	}
}
