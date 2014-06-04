package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.CostsYouLessToCast;
import org.rnd.jmagic.abilities.StaticPTChange;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Undead Warchief")
@Types({Type.CREATURE})
@SubTypes({SubType.ZOMBIE})
@ManaCost("2BB")
@Printings({@Printings.Printed(ex = Expansion.TIME_SPIRAL, r = Rarity.SPECIAL), @Printings.Printed(ex = Expansion.SCOURGE, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.BLACK})
public final class UndeadWarchief extends Card
{
	public UndeadWarchief(GameState state)
	{
		super(state);

		this.setPower(1);
		this.setToughness(1);

		// Zombie spells you cast cost (1) less to cast.
		this.addAbility(new CostsYouLessToCast(state, HasSubType.instance(SubType.ZOMBIE), "(1)", "Zombie spells you cast cost (1) less to cast."));

		// Zombie creatures you control get +2/+1.
		SetGenerator yourZombies = Intersect.instance(HasSubType.instance(SubType.ZOMBIE), CREATURES_YOU_CONTROL);
		this.addAbility(new StaticPTChange(state, yourZombies, "Zombie creatures you control", +2, +1, true));
	}
}
