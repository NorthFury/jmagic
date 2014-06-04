package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Haste;
import org.rnd.jmagic.abilities.keywords.Lifelink;
import org.rnd.jmagic.abilities.keywords.Trample;
import org.rnd.jmagic.engine.*;

@Name("Spark Trooper")
@Types({Type.CREATURE})
@SubTypes({SubType.ELEMENTAL, SubType.SOLDIER})
@ManaCost("1RRW")
@Printings({@Printings.Printed(ex = Expansion.GATECRASH, r = Rarity.RARE)})
@ColorIdentity({Color.WHITE, Color.RED})
public final class SparkTrooper extends Card
{
	public static final class SparkTrooperAbility1 extends EventTriggeredAbility
	{
		public SparkTrooperAbility1(GameState state)
		{
			super(state, "At the beginning of the end step, sacrifice Spark Trooper.");
			this.addPattern(atTheBeginningOfTheEndStep());
			this.addEffect(sacrificeThis("Spark Trooper"));
		}
	}

	public SparkTrooper(GameState state)
	{
		super(state);

		this.setPower(6);
		this.setToughness(1);

		// Trample, lifelink, haste
		this.addAbility(new Trample(state));
		this.addAbility(new Lifelink(state));
		this.addAbility(new Haste(state));

		// At the beginning of the end step, sacrifice Spark Trooper.
		this.addAbility(new SparkTrooperAbility1(state));
	}
}
