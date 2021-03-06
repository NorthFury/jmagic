package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Bloodthirst;
import org.rnd.jmagic.abilities.keywords.Flanking;
import org.rnd.jmagic.engine.*;

@Name("Bogardan Lancer")
@Types({Type.CREATURE})
@SubTypes({SubType.HUMAN, SubType.KNIGHT})
@ManaCost("1R")
@Printings({@Printings.Printed(ex = Expansion.FUTURE_SIGHT, r = Rarity.COMMON)})
@ColorIdentity({Color.RED})
public final class BogardanLancer extends Card
{
	public BogardanLancer(GameState state)
	{
		super(state);

		this.setPower(1);
		this.setToughness(1);

		this.addAbility(new Bloodthirst.Final(state, 1));
		this.addAbility(new Flanking(state));
	}
}
