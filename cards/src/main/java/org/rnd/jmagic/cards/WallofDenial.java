package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Defender;
import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.abilities.keywords.Shroud;
import org.rnd.jmagic.engine.*;

@Name("Wall of Denial")
@Types({Type.CREATURE})
@SubTypes({SubType.WALL})
@ManaCost("1WU")
@Printings({@Printings.Printed(ex = Expansion.COMMANDER, r = Rarity.UNCOMMON), @Printings.Printed(ex = Expansion.ALARA_REBORN, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.BLUE, Color.WHITE})
public final class WallofDenial extends Card
{
	public WallofDenial(GameState state)
	{
		super(state);

		this.setPower(0);
		this.setToughness(8);

		this.addAbility(new Defender(state));

		this.addAbility(new Flying(state));

		this.addAbility(new Shroud(state));
	}
}
