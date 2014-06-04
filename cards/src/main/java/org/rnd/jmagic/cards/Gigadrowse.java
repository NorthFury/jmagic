package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.Convenience;
import org.rnd.jmagic.abilities.keywords.Replicate;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.Permanents;

@Name("Gigadrowse")
@Types({Type.INSTANT})
@ManaCost("U")
@Printings({@Printings.Printed(ex = Expansion.GUILDPACT, r = Rarity.COMMON)})
@ColorIdentity({Color.BLUE})
public final class Gigadrowse extends Card
{
	public Gigadrowse(GameState state)
	{
		super(state);
		Target target = this.addTarget(Permanents.instance(), "target permanent");
		this.addAbility(new Replicate(state, "(U)"));
		this.addEffect(Convenience.tap(targetedBy(target), "Tap target permanent."));
	}
}
