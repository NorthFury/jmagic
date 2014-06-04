package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.Firebreathing;
import org.rnd.jmagic.abilities.Regenerate;
import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.abilities.keywords.Haste;
import org.rnd.jmagic.abilities.keywords.Trample;
import org.rnd.jmagic.engine.*;

@Name("Hellkite Overlord")
@Types({Type.CREATURE})
@SubTypes({SubType.DRAGON})
@ManaCost("4BRRG")
@Printings({@Printings.Printed(ex = Expansion.SHARDS_OF_ALARA, r = Rarity.MYTHIC), @Printings.Printed(ex = Expansion.DRAGONS, r = Rarity.RARE)})
@ColorIdentity({Color.GREEN, Color.BLACK, Color.RED})
public final class HellkiteOverlord extends Card
{
	public HellkiteOverlord(GameState state)
	{
		super(state);

		this.setPower(8);
		this.setToughness(8);

		// Flying, trample, haste
		this.addAbility(new Flying(state));
		this.addAbility(new Trample(state));
		this.addAbility(new Haste(state));

		// (R): Hellkite Overlord gets +1/+0 until end of turn.
		this.addAbility(new Firebreathing(state, "Hellkite Overlord"));

		// (B)(G): Regenerate Hellkite Overlord.
		this.addAbility(new Regenerate.Final(state, "(B)(G)", "Hellkite Overlord"));
	}
}
