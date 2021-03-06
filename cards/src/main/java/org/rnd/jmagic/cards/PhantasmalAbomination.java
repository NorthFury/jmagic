package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Defender;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.patterns.BecomesTheTargetPattern;

@Name("Phantasmal Abomination")
@Types({Type.CREATURE})
@SubTypes({SubType.ILLUSION})
@ManaCost("1UU")
@Printings({@Printings.Printed(ex = Expansion.RISE_OF_THE_ELDRAZI, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.BLUE})
public final class PhantasmalAbomination extends Card
{
	public static final class PhantasmalAbominationAbility1 extends EventTriggeredAbility
	{
		public PhantasmalAbominationAbility1(GameState state)
		{
			super(state, "When Phantasmal Abomination becomes the target of a spell or ability, sacrifice it.");

			EventPattern pattern = new BecomesTheTargetPattern(ABILITY_SOURCE_OF_THIS);
			this.addPattern(pattern);
			this.addEffect(sacrificeThis("Phantasmal Abomination"));
		}
	}

	public PhantasmalAbomination(GameState state)
	{
		super(state);

		this.setPower(5);
		this.setToughness(5);

		// Defender
		this.addAbility(new Defender(state));

		// When Phantasmal Abomination becomes the target of a spell or ability,
		// sacrifice it.
		this.addAbility(new PhantasmalAbominationAbility1(state));
	}
}
