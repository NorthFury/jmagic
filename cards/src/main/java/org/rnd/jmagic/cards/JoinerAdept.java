package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.TapForAnyColor;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Joiner Adept")
@Types({Type.CREATURE})
@SubTypes({SubType.DRUID, SubType.ELF})
@ManaCost("1G")
@Printings({@Printings.Printed(ex = Expansion.TENTH_EDITION, r = Rarity.RARE), @Printings.Printed(ex = Expansion.FIFTH_DAWN, r = Rarity.RARE)})
@ColorIdentity({Color.GREEN})
public final class JoinerAdept extends Card
{
	public static final class UtopiaLands extends StaticAbility
	{
		public UtopiaLands(GameState state)
		{
			super(state, "Lands you control have \"(T): Add one mana of any color to your mana pool.\"");

			Intersect landsYouControl = Intersect.instance(LandPermanents.instance(), ControlledBy.instance(You.instance()));
			this.addEffectPart(addAbilityToObject(landsYouControl, TapForAnyColor.class));
		}
	}

	public JoinerAdept(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(1);

		this.addAbility(new UtopiaLands(state));
	}
}
