package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.abilities.keywords.Hexproof;
import org.rnd.jmagic.engine.*;

@Name("Aven Fleetwing")
@Types({Type.CREATURE})
@SubTypes({SubType.SOLDIER, SubType.BIRD})
@ManaCost("3U")
@Printings({@Printings.Printed(ex = Expansion.MAGIC_2012, r = Rarity.COMMON)})
@ColorIdentity({Color.BLUE})
public final class AvenFleetwing extends Card
{
	public AvenFleetwing(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(2);

		// Flying
		this.addAbility(new Flying(state));

		// Hexproof (This creature can't be the target of spells or abilities
		// your opponents control.)
		this.addAbility(new Hexproof(state));
	}
}
