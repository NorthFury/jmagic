package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.Bloodrush;
import org.rnd.jmagic.abilities.keywords.Trample;
import org.rnd.jmagic.engine.*;

@Name("Skarrg Goliath")
@Types({Type.CREATURE})
@SubTypes({SubType.BEAST})
@ManaCost("6GG")
@Printings({@Printings.Printed(ex = Expansion.GATECRASH, r = Rarity.RARE)})
@ColorIdentity({Color.GREEN})
public final class SkarrgGoliath extends Card
{
	public SkarrgGoliath(GameState state)
	{
		super(state);

		this.setPower(9);
		this.setToughness(9);

		// Trample
		this.addAbility(new Trample(state));

		// Bloodrush \u2014 (5)(G)(G), Discard Skarrg Goliath: Target attacking
		// creature gets +9/+9 and gains trample until end of turn.
		this.addAbility(new Bloodrush(state, "(5)(G)(G)", "Skarrg Goliath", +9, +9, "Target attacking creature gets +9/+9 and gains trample until end of turn.", Trample.class));
	}
}
