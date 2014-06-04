package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.ColossusShuffle;
import org.rnd.jmagic.abilities.keywords.Indestructible;
import org.rnd.jmagic.abilities.keywords.Infect;
import org.rnd.jmagic.abilities.keywords.Trample;
import org.rnd.jmagic.engine.*;

@Name("Blightsteel Colossus")
@Types({Type.CREATURE, Type.ARTIFACT})
@SubTypes({SubType.GOLEM})
@ManaCost("(12)")
@Printings({@Printings.Printed(ex = Expansion.MIRRODIN_BESIEGED, r = Rarity.MYTHIC)})
@ColorIdentity({})
public final class BlightsteelColossus extends Card
{
	public BlightsteelColossus(GameState state)
	{
		super(state);

		this.setPower(11);
		this.setToughness(11);

		// Trample, infect
		this.addAbility(new Trample(state));
		this.addAbility(new Infect(state));

		// Blightsteel Colossus is indestructible.
		this.addAbility(new Indestructible(state));

		// If Blightsteel Colossus would be put into a graveyard from anywhere,
		// reveal Blightsteel Colossus and shuffle it into its owner's library
		// instead.
		this.addAbility(new ColossusShuffle(state, this.getName()));
	}
}
