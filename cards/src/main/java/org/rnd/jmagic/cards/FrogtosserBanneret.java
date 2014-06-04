package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.CostsYouLessToCast;
import org.rnd.jmagic.abilities.keywords.Haste;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Frogtosser Banneret")
@Types({Type.CREATURE})
@SubTypes({SubType.ROGUE, SubType.GOBLIN})
@ManaCost("1B")
@Printings({@Printings.Printed(ex = Expansion.MORNINGTIDE, r = Rarity.COMMON)})
@ColorIdentity({Color.BLACK})
public final class FrogtosserBanneret extends Card
{
	public FrogtosserBanneret(GameState state)
	{
		super(state);

		this.setPower(1);
		this.setToughness(1);

		this.addAbility(new Haste(state));

		this.addAbility(new CostsYouLessToCast(state, HasSubType.instance(SubType.GOBLIN, SubType.ROGUE), "(1)", "Goblin spells and Rogue spells you cast cost (1) less to cast."));
	}
}
