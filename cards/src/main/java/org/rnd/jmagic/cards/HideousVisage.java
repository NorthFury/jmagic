package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Intimidate;
import org.rnd.jmagic.engine.*;

@Name("Hideous Visage")
@Types({Type.SORCERY})
@ManaCost("2B")
@Printings({@Printings.Printed(ex = Expansion.MAGIC_2012, r = Rarity.COMMON)})
@ColorIdentity({Color.BLACK})
public final class HideousVisage extends Card
{
	public HideousVisage(GameState state)
	{
		super(state);

		// Creatures you control gain intimidate until end of turn. (Each of
		// those creatures can't be blocked except by artifact creatures and/or
		// creatures that share a color with it.)
		this.addEffect(addAbilityUntilEndOfTurn(CREATURES_YOU_CONTROL, Intimidate.class, "Creatures you control gain intimidate until end of turn."));
	}
}
