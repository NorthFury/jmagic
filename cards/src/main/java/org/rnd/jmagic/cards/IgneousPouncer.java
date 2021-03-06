package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Haste;
import org.rnd.jmagic.abilities.keywords.TypeCycling;
import org.rnd.jmagic.engine.*;

@Name("Igneous Pouncer")
@Types({Type.CREATURE})
@SubTypes({SubType.ELEMENTAL})
@ManaCost("4BR")
@Printings({@Printings.Printed(ex = Expansion.ALARA_REBORN, r = Rarity.COMMON)})
@ColorIdentity({Color.BLACK, Color.RED})
public final class IgneousPouncer extends Card
{
	public IgneousPouncer(GameState state)
	{
		super(state);

		this.setPower(5);
		this.setToughness(1);

		this.addAbility(new Haste(state));

		this.addAbility(new TypeCycling.SwampCycling(state, "(2)"));
		this.addAbility(new TypeCycling.MountainCycling(state, "(2)"));
	}
}
