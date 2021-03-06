package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Defender;
import org.rnd.jmagic.abilities.keywords.TypeCycling;
import org.rnd.jmagic.engine.*;

@Name("Sanctum Plowbeast")
@Types({Type.ARTIFACT, Type.CREATURE})
@SubTypes({SubType.BEAST})
@ManaCost("4WU")
@Printings({@Printings.Printed(ex = Expansion.ALARA_REBORN, r = Rarity.COMMON)})
@ColorIdentity({Color.BLUE, Color.WHITE})
public final class SanctumPlowbeast extends Card
{
	public SanctumPlowbeast(GameState state)
	{
		super(state);

		this.setPower(3);
		this.setToughness(6);

		this.addAbility(new Defender(state));

		this.addAbility(new TypeCycling.PlainsCycling(state, "(2)"));
		this.addAbility(new TypeCycling.IslandCycling(state, "(2)"));
	}
}
