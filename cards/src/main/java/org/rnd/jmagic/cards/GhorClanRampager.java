package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.Bloodrush;
import org.rnd.jmagic.abilities.keywords.Trample;
import org.rnd.jmagic.engine.*;

@Name("Ghor-Clan Rampager")
@Types({Type.CREATURE})
@SubTypes({SubType.BEAST})
@ManaCost("2RG")
@Printings({@Printings.Printed(ex = Expansion.GATECRASH, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.GREEN, Color.RED})
public final class GhorClanRampager extends Card
{
	public GhorClanRampager(GameState state)
	{
		super(state);

		this.setPower(4);
		this.setToughness(4);

		// Trample
		this.addAbility(new Trample(state));

		// Bloodrush \u2014 (R)(G), Discard Ghor-Clan Rampager: Target attacking
		// creature gets +4/+4 and gains trample until end of turn.
		this.addAbility(new Bloodrush(state, "(R)(G)", "Ghor-Clan Rampager", +4, +4, "Target attacking creature gets +4/+4 and gains trample until end of turn.", Trample.class));
	}
}
