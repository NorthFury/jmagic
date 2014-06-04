package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Defender;
import org.rnd.jmagic.abilities.keywords.Protection;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Grave Bramble")
@Types({Type.CREATURE})
@SubTypes({SubType.PLANT})
@ManaCost("1GG")
@Printings({@Printings.Printed(ex = Expansion.INNISTRAD, r = Rarity.COMMON)})
@ColorIdentity({Color.GREEN})
public final class GraveBramble extends Card
{
	public GraveBramble(GameState state)
	{
		super(state);

		this.setPower(3);
		this.setToughness(4);

		// Defender, protection from Zombies
		this.addAbility(new Defender(state));
		SetGenerator zombies = Intersect.instance(HasSubType.instance(SubType.ZOMBIE), Permanents.instance());
		this.addAbility(new Protection.From(state, zombies, "Zombies"));
	}
}
