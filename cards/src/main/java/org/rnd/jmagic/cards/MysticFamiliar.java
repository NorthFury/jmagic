package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.abilities.keywords.Protection;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Mystic Familiar")
@Types({Type.CREATURE})
@SubTypes({SubType.BIRD})
@ManaCost("1W")
@Printings({@Printings.Printed(ex = Expansion.TORMENT, r = Rarity.COMMON)})
@ColorIdentity({Color.WHITE})
public final class MysticFamiliar extends Card
{
	public static final class ThresholdRacism extends StaticAbility
	{
		public ThresholdRacism(GameState state)
		{
			super(state, "As long as seven or more cards are in your graveyard, Mystic Familiar gets +1/+1 and has protection from black.");

			this.addEffectPart(modifyPowerAndToughness(This.instance(), +1, +1));

			this.addEffectPart(addAbilityToObject(This.instance(), Protection.FromBlack.class));

			this.canApply = Both.instance(this.canApply, Threshold.instance());
		}
	}

	public MysticFamiliar(GameState state)
	{
		super(state);

		this.setPower(1);
		this.setToughness(2);

		this.addAbility(new Flying(state));

		this.addAbility(new ThresholdRacism(state));
	}
}
