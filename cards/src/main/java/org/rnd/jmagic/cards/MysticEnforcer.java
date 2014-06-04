package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.abilities.keywords.Protection;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Mystic Enforcer")
@Types({Type.CREATURE})
@SubTypes({SubType.HUMAN, SubType.MYSTIC, SubType.NOMAD})
@ManaCost("2GW")
@Printings({@Printings.Printed(ex = Expansion.TIME_SPIRAL, r = Rarity.SPECIAL), @Printings.Printed(ex = Expansion.ODYSSEY, r = Rarity.RARE)})
@ColorIdentity({Color.WHITE, Color.GREEN})
public final class MysticEnforcer extends Card
{
	public static final class AngelicThreshold extends StaticAbility
	{
		public AngelicThreshold(GameState state)
		{
			super(state, "As long as seven or more cards are in your graveyard, Mystic Enforcer gets +3/+3 and has flying.");

			this.addEffectPart(modifyPowerAndToughness(This.instance(), +3, +3));

			this.addEffectPart(addAbilityToObject(This.instance(), Flying.class));

			this.canApply = Both.instance(this.canApply, Threshold.instance());
		}
	}

	public MysticEnforcer(GameState state)
	{
		super(state);

		this.setPower(3);
		this.setToughness(3);

		this.addAbility(new Protection.FromBlack(state));

		this.addAbility(new AngelicThreshold(state));
	}
}
