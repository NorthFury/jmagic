package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Defender;
import org.rnd.jmagic.abilities.keywords.Reach;
import org.rnd.jmagic.engine.*;

@Name("Wall of Vines")
@Types({Type.CREATURE})
@SubTypes({SubType.PLANT, SubType.WALL})
@ManaCost("G")
@Printings({@Printings.Printed(ex = Expansion.MAGIC_2011, r = Rarity.COMMON)})
@ColorIdentity({Color.GREEN})
public final class WallofVines extends Card
{
	public WallofVines(GameState state)
	{
		super(state);

		this.setPower(0);
		this.setToughness(3);

		// Defender (This creature can't attack.)
		this.addAbility(new Defender(state));

		// Reach (This creature can block creatures with flying.)
		this.addAbility(new Reach(state));
	}
}
