package org.rnd.jmagic.cards;

import org.rnd.jmagic.cardTemplates.ShardsHerald;
import org.rnd.jmagic.engine.*;

@Name("Sphinx's Herald")
@Types({Type.CREATURE, Type.ARTIFACT})
@SubTypes({SubType.VEDALKEN, SubType.WIZARD})
@ManaCost("U")
@Printings({@Printings.Printed(ex = Expansion.SHARDS_OF_ALARA, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.BLUE})
public final class SphinxsHerald extends ShardsHerald
{
	public SphinxsHerald(GameState state)
	{
		super(state, Color.WHITE, Color.BLUE, Color.BLACK, "Sphinx Sovereign");

		this.setPower(1);
		this.setToughness(1);

		// (2)(U), (T), Sacrifice a white creature, a blue creature, and a black
		// creature: Search your library for a card named Sphinx Sovereign and
		// put it onto the battlefield. Then shuffle your library.
	}
}
