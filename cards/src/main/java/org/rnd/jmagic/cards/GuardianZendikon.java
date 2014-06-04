package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.EnchantedCardComesBackToHand;
import org.rnd.jmagic.abilities.StaticAnimation;
import org.rnd.jmagic.abilities.keywords.Defender;
import org.rnd.jmagic.abilities.keywords.Enchant;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Guardian Zendikon")
@Types({Type.ENCHANTMENT})
@SubTypes({SubType.AURA})
@ManaCost("2W")
@Printings({@Printings.Printed(ex = Expansion.WORLDWAKE, r = Rarity.COMMON)})
@ColorIdentity({Color.WHITE})
public final class GuardianZendikon extends Card
{
	public GuardianZendikon(GameState state)
	{
		super(state);

		// Enchant land
		this.addAbility(new Enchant.Land(state));

		// Enchanted land is a 2/6 white Wall creature with defender. It's still
		// a land.
		Animator animator = new Animator(EnchantedBy.instance(This.instance()), 2, 6);
		animator.addColor(Color.WHITE);
		animator.addSubType(SubType.WALL);
		animator.addAbility(Defender.class);
		this.addAbility(new StaticAnimation(state, animator, "Enchanted land is a 2/6 white Wall creature with defender. It's still a land."));

		// When enchanted land is put into a graveyard, return that card to its
		// owner's hand.
		this.addAbility(new EnchantedCardComesBackToHand(state, "enchanted land"));
	}
}
