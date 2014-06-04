package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.EntersTheBattlefieldTapped;
import org.rnd.jmagic.abilities.TapForW;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.You;

@Name("Kabira Crossroads")
@Types({Type.LAND})
@Printings({@Printings.Printed(ex = Expansion.ZENDIKAR, r = Rarity.COMMON)})
@ColorIdentity({Color.WHITE})
public final class KabiraCrossroads extends Card
{
	public static final class KabiraLife extends EventTriggeredAbility
	{
		public KabiraLife(GameState state)
		{
			super(state, "When Kabira Crossroads enters the battlefield, you gain 2 life.");
			this.addPattern(whenThisEntersTheBattlefield());
			this.addEffect(gainLife(You.instance(), 2, "You gain 2 life"));
		}
	}

	public KabiraCrossroads(GameState state)
	{
		super(state);

		this.addAbility(new EntersTheBattlefieldTapped(state, "Kabira Crossroads"));

		this.addAbility(new KabiraLife(state));

		this.addAbility(new TapForW(state));
	}
}
