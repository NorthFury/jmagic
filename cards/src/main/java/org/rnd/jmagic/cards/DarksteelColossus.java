package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.ColossusShuffle;
import org.rnd.jmagic.abilities.keywords.Indestructible;
import org.rnd.jmagic.abilities.keywords.Trample;
import org.rnd.jmagic.engine.*;

@Name("Darksteel Colossus")
@Types({Type.CREATURE, Type.ARTIFACT})
@SubTypes({SubType.GOLEM})
@ManaCost("(11)")
@Printings({@Printings.Printed(ex = Expansion.MAGIC_2010, r = Rarity.MYTHIC), @Printings.Printed(ex = Expansion.DARKSTEEL, r = Rarity.RARE)})
@ColorIdentity({})
public final class DarksteelColossus extends Card
{
	public DarksteelColossus(GameState state)
	{
		super(state);

		this.setPower(11);
		this.setToughness(11);

		// Trample
		this.addAbility(new Trample(state));

		// Darksteel Colossus is indestructible.
		this.addAbility(new Indestructible(state));

		this.addAbility(new ColossusShuffle(state, this.getName()));
	}
}
