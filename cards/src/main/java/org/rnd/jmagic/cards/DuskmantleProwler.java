package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Exalted;
import org.rnd.jmagic.abilities.keywords.Haste;
import org.rnd.jmagic.engine.*;

@Name("Duskmantle Prowler")
@Types({Type.CREATURE})
@SubTypes({SubType.VAMPIRE, SubType.ROGUE})
@ManaCost("3B")
@Printings({@Printings.Printed(ex = Expansion.MAGIC_2013, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.BLACK})
public final class DuskmantleProwler extends Card
{
	public DuskmantleProwler(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(2);

		// Haste (This creature can attack and (T) as soon as it comes under
		// your control.)
		this.addAbility(new Haste(state));

		// Exalted (Whenever a creature you control attacks alone, that creature
		// gets +1/+1 until end of turn.)
		this.addAbility(new Exalted(state));
	}
}
