package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Cipher;
import org.rnd.jmagic.engine.*;

@Name("Last Thoughts")
@Types({Type.SORCERY})
@ManaCost("3U")
@Printings({@Printings.Printed(ex = Expansion.GATECRASH, r = Rarity.COMMON)})
@ColorIdentity({Color.BLUE})
public final class LastThoughts extends Card
{
	public LastThoughts(GameState state)
	{
		super(state);

		// Draw a card.
		this.addEffect(drawACard());

		// Cipher (Then you may exile this spell card encoded on a creature you
		// control. Whenever that creature deals combat damage to a player, its
		// controller may cast a copy of the encoded card without paying its
		// mana cost.)
		this.addAbility(new Cipher(state));
	}
}
