package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Esper Charm")
@Types({Type.INSTANT})
@ManaCost("WUB")
@Printings({@Printings.Printed(ex = Expansion.SHARDS_OF_ALARA, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.BLUE, Color.WHITE, Color.BLACK})
public final class EsperCharm extends Card
{
	public EsperCharm(GameState state)
	{
		super(state);

		// Destroy target enchantment;
		{
			Target target = this.addTarget(1, EnchantmentPermanents.instance(), "target enchantment");
			this.addEffect(1, destroy(targetedBy(target), "Destroy target enchantment"));
		}

		// or draw two cards;
		{
			this.addEffect(2, drawCards(You.instance(), 2, "draw two cards"));
		}

		// or target player discards two cards.
		{
			Target target = this.addTarget(3, Players.instance(), "target player");
			this.addEffect(3, discardCards(targetedBy(target), 2, "target player discards two cards."));
		}
	}
}
