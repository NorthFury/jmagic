package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Intimidate;
import org.rnd.jmagic.engine.*;

@Name("Spectral Rider")
@Types({Type.CREATURE})
@SubTypes({SubType.SPIRIT, SubType.KNIGHT})
@ManaCost("WW")
@Printings({@Printings.Printed(ex = Expansion.INNISTRAD, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.WHITE})
public final class SpectralRider extends Card
{
	public SpectralRider(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(2);

		// Intimidate (This creature can't be blocked except by artifact
		// creatures and/or creatures that share a color with it.)
		this.addAbility(new Intimidate(state));
	}
}
