package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Convoke;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Devouring Light")
@Types({Type.INSTANT})
@ManaCost("1WW")
@Printings({@Printings.Printed(ex = Expansion.RAVNICA, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.WHITE})
public final class DevouringLight extends Card
{
	public DevouringLight(GameState state)
	{
		super(state);

		// Convoke
		this.addAbility(new Convoke(state));

		// Exile target attacking or blocking creature.
		Target target = this.addTarget(Union.instance(Attacking.instance(), Blocking.instance()), "target attacking or blocking creature");
		this.addEffect(exile(targetedBy(target), "Exile target attacking or blocking creature."));
	}
}
