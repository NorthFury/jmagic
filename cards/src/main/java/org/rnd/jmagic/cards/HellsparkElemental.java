package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Haste;
import org.rnd.jmagic.abilities.keywords.Trample;
import org.rnd.jmagic.abilities.keywords.Unearth;
import org.rnd.jmagic.engine.*;

@Name("Hellspark Elemental")
@Types({Type.CREATURE})
@SubTypes({SubType.ELEMENTAL})
@ManaCost("1R")
@Printings({@Printings.Printed(ex = Expansion.CONFLUX, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.RED})
public final class HellsparkElemental extends Card
{
	public static final class Fizzle extends EventTriggeredAbility
	{
		public Fizzle(GameState state)
		{
			super(state, "At the beginning of the end step, sacrifice Hellspark Elemental.");
			this.addPattern(atTheBeginningOfTheEndStep());
			this.addEffect(sacrificeThis("Hellspark Elemental"));
		}
	}

	public HellsparkElemental(GameState state)
	{
		super(state);

		this.setPower(3);
		this.setToughness(1);

		this.addAbility(new Trample(state));
		this.addAbility(new Haste(state));
		this.addAbility(new Fizzle(state));
		this.addAbility(new Unearth(state, "(1)(R)"));
	}
}
