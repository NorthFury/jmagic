package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.DoubleStrike;
import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.abilities.keywords.Lifelink;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;
import org.rnd.jmagic.engine.patterns.SimpleEventPattern;

@Name("Drogskol Reaver")
@Types({Type.CREATURE})
@SubTypes({SubType.SPIRIT})
@ManaCost("5WU")
@Printings({@Printings.Printed(ex = Expansion.DARK_ASCENSION, r = Rarity.MYTHIC)})
@ColorIdentity({Color.BLUE, Color.WHITE})
public final class DrogskolReaver extends Card
{
	public static final class DrogskolReaverAbility1 extends EventTriggeredAbility
	{
		public DrogskolReaverAbility1(GameState state)
		{
			super(state, "Whenever you gain life, draw a card.");

			SimpleEventPattern pattern = new SimpleEventPattern(EventType.GAIN_LIFE_ONE_PLAYER);
			pattern.put(EventType.Parameter.PLAYER, You.instance());
			this.addPattern(pattern);

			this.addEffect(drawACard());
		}
	}

	public DrogskolReaver(GameState state)
	{
		super(state);

		this.setPower(3);
		this.setToughness(5);

		// Flying, double strike, lifelink
		this.addAbility(new Flying(state));
		this.addAbility(new DoubleStrike(state));
		this.addAbility(new Lifelink(state));

		// Whenever you gain life, draw a card.
		this.addAbility(new DrogskolReaverAbility1(state));
	}
}
