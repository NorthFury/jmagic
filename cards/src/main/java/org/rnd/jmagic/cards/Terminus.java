package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Miracle;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Terminus")
@Types({Type.SORCERY})
@ManaCost("4WW")
@Printings({@Printings.Printed(ex = Expansion.AVACYN_RESTORED, r = Rarity.RARE)})
@ColorIdentity({Color.WHITE})
public final class Terminus extends Card
{
	public Terminus(GameState state)
	{
		super(state);

		// Put all creatures on the bottom of their owners' libraries.
		this.addEffect(putOnBottomOfLibrary(CreaturePermanents.instance(), "Put all creatures on the bottom of their owners' libraries."));

		// Miracle (W)
		this.addAbility(new Miracle(state, "(W)"));
	}
}
