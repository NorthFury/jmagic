package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.abilities.keywords.Haste;
import org.rnd.jmagic.abilities.keywords.Unearth;
import org.rnd.jmagic.engine.*;

@Name("Hell's Thunder")
@Types({Type.CREATURE})
@SubTypes({SubType.ELEMENTAL})
@ManaCost("1RR")
@Printings({@Printings.Printed(ex = Expansion.SHARDS_OF_ALARA, r = Rarity.RARE)})
@ColorIdentity({Color.RED})
public final class HellsThunder extends Card
{
	public static final class Fizzle extends EventTriggeredAbility
	{
		public Fizzle(GameState state)
		{
			super(state, "At the beginning of the end step, sacrifice Hell's Thunder.");
			this.addPattern(atTheBeginningOfTheEndStep());
			this.addEffect(sacrificeThis("Hell's Thunder"));
		}
	}

	public HellsThunder(GameState state)
	{
		super(state);

		this.setPower(4);
		this.setToughness(4);

		this.addAbility(new Flying(state));
		this.addAbility(new Haste(state));
		this.addAbility(new Fizzle(state));
		this.addAbility(new Unearth(state, "(4)(R)"));
	}
}
