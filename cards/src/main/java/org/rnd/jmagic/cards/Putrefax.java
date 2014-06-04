package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Haste;
import org.rnd.jmagic.abilities.keywords.Infect;
import org.rnd.jmagic.abilities.keywords.Trample;
import org.rnd.jmagic.engine.*;

@Name("Putrefax")
@Types({Type.CREATURE})
@SubTypes({SubType.HORROR})
@ManaCost("3GG")
@Printings({@Printings.Printed(ex = Expansion.SCARS_OF_MIRRODIN, r = Rarity.RARE)})
@ColorIdentity({Color.GREEN})
public final class Putrefax extends Card
{
	public static final class PutrefaxAbility2 extends EventTriggeredAbility
	{
		public PutrefaxAbility2(GameState state)
		{
			super(state, "At the beginning of the end step, sacrifice Putrefax.");
			this.addPattern(atTheBeginningOfTheEndStep());
			this.addEffect(sacrificeThis("Putrefax"));
		}
	}

	public Putrefax(GameState state)
	{
		super(state);

		this.setPower(5);
		this.setToughness(3);

		// Trample, haste
		this.addAbility(new Trample(state));
		this.addAbility(new Haste(state));

		// Infect (This creature deals damage to creatures in the form of -1/-1
		// counters and to players in the form of poison counters.)
		this.addAbility(new Infect(state));

		// At the beginning of the end step, sacrifice Putrefax.
		this.addAbility(new PutrefaxAbility2(state));
	}
}
