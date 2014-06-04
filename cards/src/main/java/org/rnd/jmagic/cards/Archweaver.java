package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Reach;
import org.rnd.jmagic.abilities.keywords.Trample;
import org.rnd.jmagic.engine.*;

@Name("Archweaver")
@Types({Type.CREATURE})
@SubTypes({SubType.SPIDER})
@ManaCost("5GG")
@Printings({@Printings.Printed(ex = Expansion.RETURN_TO_RAVNICA, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.GREEN})
public final class Archweaver extends Card
{
	public Archweaver(GameState state)
	{
		super(state);

		this.setPower(5);
		this.setToughness(5);

		// Reach, trample
		this.addAbility(new Reach(state));
		this.addAbility(new Trample(state));
	}
}
