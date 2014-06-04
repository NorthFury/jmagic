package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Defender;
import org.rnd.jmagic.abilities.keywords.Exalted;
import org.rnd.jmagic.engine.*;

@Name("Guardians of Akrasa")
@Types({Type.CREATURE})
@SubTypes({SubType.HUMAN, SubType.SOLDIER})
@ManaCost("2W")
@Printings({@Printings.Printed(ex = Expansion.MAGIC_2013, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.SHARDS_OF_ALARA, r = Rarity.COMMON)})
@ColorIdentity({Color.WHITE})
public final class GuardiansofAkrasa extends Card
{
	public GuardiansofAkrasa(GameState state)
	{
		super(state);

		this.setPower(0);
		this.setToughness(4);

		this.addAbility(new Defender(state));

		this.addAbility(new Exalted(state));
	}
}
