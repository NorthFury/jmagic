package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Haste;
import org.rnd.jmagic.abilities.keywords.Trample;
import org.rnd.jmagic.engine.*;

@Name("Spark Elemental")
@Types({Type.CREATURE})
@SubTypes({SubType.ELEMENTAL})
@ManaCost("R")
@Printings({@Printings.Printed(ex = Expansion.TENTH_EDITION, r = Rarity.UNCOMMON), @Printings.Printed(ex = Expansion.FIFTH_DAWN, r = Rarity.COMMON)})
@ColorIdentity({Color.RED})
public final class SparkElemental extends Card
{
	public static final class Fizzle extends EventTriggeredAbility
	{
		public Fizzle(GameState state)
		{
			super(state, "At the beginning of the end step, sacrifice Spark Elemental.");
			this.addPattern(atTheBeginningOfTheEndStep());
			this.addEffect(sacrificeThis("Spark Elemental"));
		}
	}

	public SparkElemental(GameState state)
	{
		super(state);

		this.setPower(3);
		this.setToughness(1);

		this.addAbility(new Trample(state));
		this.addAbility(new Haste(state));
		this.addAbility(new Fizzle(state));
	}
}
