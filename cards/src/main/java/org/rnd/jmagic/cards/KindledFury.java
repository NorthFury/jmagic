package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.FirstStrike;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Kindled Fury")
@Types({Type.INSTANT})
@ManaCost("R")
@Printings({@Printings.Printed(ex = Expansion.MAGIC_2013, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.MAGIC_2010, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.MORNINGTIDE, r = Rarity.COMMON)})
@ColorIdentity({Color.RED})
public final class KindledFury extends Card
{
	public KindledFury(GameState state)
	{
		super(state);

		Target target = this.addTarget(CreaturePermanents.instance(), "target creature");
		this.addEffect(ptChangeAndAbilityUntilEndOfTurn(targetedBy(target), +1, +0, "Target creature gets +1/+0 and gains first strike until end of turn.", FirstStrike.class));
	}
}
