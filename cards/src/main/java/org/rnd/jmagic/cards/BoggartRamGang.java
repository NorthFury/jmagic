package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Haste;
import org.rnd.jmagic.abilities.keywords.Wither;
import org.rnd.jmagic.engine.*;

@Name("Boggart Ram-Gang")
@Types({Type.CREATURE})
@SubTypes({SubType.GOBLIN, SubType.WARRIOR})
@ManaCost("(RG)(RG)(RG)")
@Printings({@Printings.Printed(ex = Expansion.SHADOWMOOR, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.GREEN, Color.RED})
public final class BoggartRamGang extends Card
{
	public BoggartRamGang(GameState state)
	{
		super(state);

		this.setPower(3);
		this.setToughness(3);

		this.addAbility(new Haste(state));
		this.addAbility(new Wither(state));
	}
}
