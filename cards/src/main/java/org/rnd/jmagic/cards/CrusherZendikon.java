package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.EnchantedCardComesBackToHand;
import org.rnd.jmagic.abilities.StaticAnimation;
import org.rnd.jmagic.abilities.keywords.Enchant;
import org.rnd.jmagic.abilities.keywords.Trample;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Crusher Zendikon")
@Types({Type.ENCHANTMENT})
@SubTypes({SubType.AURA})
@ManaCost("2R")
@Printings({@Printings.Printed(ex = Expansion.WORLDWAKE, r = Rarity.COMMON)})
@ColorIdentity({Color.RED})
public final class CrusherZendikon extends Card
{
	public CrusherZendikon(GameState state)
	{
		super(state);

		// Enchant land
		this.addAbility(new Enchant.Land(state));

		// Enchanted land is a 4/2 red Beast creature with trample. It's still a
		// land.
		Animator animator = new Animator(EnchantedBy.instance(This.instance()), 4, 2);
		animator.addColor(Color.RED);
		animator.addSubType(SubType.BEAST);
		animator.addAbility(Trample.class);
		this.addAbility(new StaticAnimation(state, animator, "Enchanted land is a 4/2 red Beast creature with trample. It's still a land."));

		// When enchanted land is put into a graveyard, return that card to its
		// owner's hand.
		this.addAbility(new EnchantedCardComesBackToHand(state, "enchanted land"));
	}
}
