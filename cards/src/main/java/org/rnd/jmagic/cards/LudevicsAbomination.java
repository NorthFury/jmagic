package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Trample;
import org.rnd.jmagic.engine.*;

@Name("Ludevic's Abomination")
@Types({Type.CREATURE})
@SubTypes({SubType.HORROR, SubType.LIZARD})
@Printings({@Printings.Printed(ex = Expansion.INNISTRAD, r = Rarity.RARE)})
@ColorIdentity({Color.BLUE})
public final class LudevicsAbomination extends AlternateCard
{
	public LudevicsAbomination(GameState state)
	{
		super(state);

		this.setPower(13);
		this.setToughness(13);

		this.setColorIndicator(Color.BLUE);

		// Trample
		this.addAbility(new Trample(state));
	}
}
