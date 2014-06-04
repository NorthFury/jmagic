package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.TapForG;
import org.rnd.jmagic.abilities.keywords.Vigilance;
import org.rnd.jmagic.engine.*;

@Name("Steward of Valeron")
@Types({Type.CREATURE})
@SubTypes({SubType.DRUID, SubType.HUMAN, SubType.KNIGHT})
@ManaCost("GW")
@Printings({@Printings.Printed(ex = Expansion.SHARDS_OF_ALARA, r = Rarity.COMMON)})
@ColorIdentity({Color.WHITE, Color.GREEN})
public final class StewardofValeron extends Card
{
	public StewardofValeron(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(2);

		this.addAbility(new Vigilance(state));

		this.addAbility(new TapForG(state));
	}
}
