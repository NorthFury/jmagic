package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.PreventCombatDamage;
import org.rnd.jmagic.abilities.keywords.Cycling;
import org.rnd.jmagic.engine.*;

@Name("Angelsong")
@Types({Type.INSTANT})
@ManaCost("1W")
@Printings({@Printings.Printed(ex = Expansion.SHARDS_OF_ALARA, r = Rarity.COMMON)})
@ColorIdentity({Color.WHITE})
public final class Angelsong extends Card
{
	public Angelsong(GameState state)
	{
		super(state);
		this.addEffect(createFloatingReplacement(new PreventCombatDamage(this.game), "Prevent all combat damage that would be dealt this turn."));
		this.addAbility(new Cycling(state, "(2)"));
	}
}
