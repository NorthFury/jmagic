package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Essence Drain")
@Types({Type.SORCERY})
@ManaCost("4B")
@Printings({@Printings.Printed(ex = Expansion.MAGIC_2013, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.TENTH_EDITION, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.DARKSTEEL, r = Rarity.COMMON)})
@ColorIdentity({Color.BLACK})
public final class EssenceDrain extends Card
{
	public EssenceDrain(GameState state)
	{
		super(state);

		Target target = this.addTarget(CREATURES_AND_PLAYERS, "target creature or player");

		this.addEffect(spellDealDamage(3, targetedBy(target), "Essence Drain deals 3 damage to target creature or player"));
		this.addEffect(gainLife(You.instance(), 3, "and you gain 3 life."));
	}
}
