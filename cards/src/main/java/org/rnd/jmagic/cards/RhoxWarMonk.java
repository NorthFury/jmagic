package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Lifelink;
import org.rnd.jmagic.engine.*;

@Name("Rhox War Monk")
@Types({Type.CREATURE})
@SubTypes({SubType.MONK, SubType.RHINO})
@ManaCost("GWU")
@Printings({@Printings.Printed(ex = Expansion.SHARDS_OF_ALARA, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.BLUE, Color.WHITE, Color.GREEN})
public final class RhoxWarMonk extends Card
{
	public RhoxWarMonk(GameState state)
	{
		super(state);

		this.setPower(3);
		this.setToughness(4);

		// Lifelink
		this.addAbility(new Lifelink(state));
	}
}
