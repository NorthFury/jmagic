package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Shroud;
import org.rnd.jmagic.engine.*;

@Name("Algae Gharial")
@Types({Type.CREATURE})
@SubTypes({SubType.CROCODILE})
@ManaCost("3G")
@Printings({@Printings.Printed(ex = Expansion.SHARDS_OF_ALARA, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.GREEN})
public final class AlgaeGharial extends Card
{
	public static final class AlgaeGharialAbility1 extends EventTriggeredAbility
	{
		public AlgaeGharialAbility1(GameState state)
		{
			super(state, "Whenever another creature dies, you may put a +1/+1 counter on Algae Gharial.");
			this.addPattern(whenAnotherCreatureIsPutIntoAGraveyardFromTheBattlefield());

			EventFactory putCounters = putCounters(1, Counter.CounterType.PLUS_ONE_PLUS_ONE, ABILITY_SOURCE_OF_THIS, "Put a +1/+1 counter on Algae Gharial.");
			this.addEffect(youMay(putCounters, "You may put a +1/+1 counter on Algae Gharial."));
		}
	}

	public AlgaeGharial(GameState state)
	{
		super(state);

		this.setPower(1);
		this.setToughness(1);

		// Shroud
		this.addAbility(new Shroud(state));

		// Whenever another creature is put into a graveyard from the
		// battlefield, you may put a +1/+1 counter on Algae Gharial.
		this.addAbility(new AlgaeGharialAbility1(state));
	}
}
