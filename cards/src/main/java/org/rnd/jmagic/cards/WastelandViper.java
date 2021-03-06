package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.Bloodrush;
import org.rnd.jmagic.abilities.keywords.Deathtouch;
import org.rnd.jmagic.engine.*;

@Name("Wasteland Viper")
@Types({Type.CREATURE})
@SubTypes({SubType.SNAKE})
@ManaCost("G")
@Printings({@Printings.Printed(ex = Expansion.GATECRASH, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.GREEN})
public final class WastelandViper extends Card
{
	public WastelandViper(GameState state)
	{
		super(state);

		this.setPower(1);
		this.setToughness(2);

		// Deathtouch
		this.addAbility(new Deathtouch(state));

		// Bloodrush \u2014 (G), Discard Wasteland Viper: Target attacking
		// creature gets +1/+2 and gains deathtouch until end of turn.
		this.addAbility(new Bloodrush(state, "(G)", "Wasteland Viper", +1, +2, "Target attacking creature gets +1/+2 and gains deathtouch until end of turn.", Deathtouch.class));
	}
}
