package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Defender;
import org.rnd.jmagic.engine.*;

@Name("Ogre Sentry")
@Types({Type.CREATURE})
@SubTypes({SubType.WARRIOR, SubType.OGRE})
@ManaCost("1R")
@Printings({@Printings.Printed(ex = Expansion.RISE_OF_THE_ELDRAZI, r = Rarity.COMMON)})
@ColorIdentity({Color.RED})
public final class OgreSentry extends Card
{
	public OgreSentry(GameState state)
	{
		super(state);

		this.setPower(3);
		this.setToughness(3);

		// Defender
		this.addAbility(new Defender(state));
	}
}
