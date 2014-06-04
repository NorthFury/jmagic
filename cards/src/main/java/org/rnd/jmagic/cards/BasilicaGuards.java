package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Defender;
import org.rnd.jmagic.abilities.keywords.Extort;
import org.rnd.jmagic.engine.*;

@Name("Basilica Guards")
@Types({Type.CREATURE})
@SubTypes({SubType.HUMAN, SubType.SOLDIER})
@ManaCost("2W")
@Printings({@Printings.Printed(ex = Expansion.GATECRASH, r = Rarity.COMMON)})
@ColorIdentity({Color.WHITE})
public final class BasilicaGuards extends Card
{
	public BasilicaGuards(GameState state)
	{
		super(state);

		this.setPower(1);
		this.setToughness(4);

		// Defender
		this.addAbility(new Defender(state));

		// Extort (Whenever you cast a spell, you may pay (w/b). If you do, each
		// opponent loses 1 life and you gain that much life.)
		this.addAbility(new Extort(state));
	}
}
