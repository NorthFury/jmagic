package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Terra Eternal")
@Types({Type.ENCHANTMENT})
@ManaCost("2W")
@Printings({@Printings.Printed(ex = Expansion.WORLDWAKE, r = Rarity.RARE)})
@ColorIdentity({Color.WHITE})
public final class TerraEternal extends Card
{
	public static final class TerraEternalAbility0 extends StaticAbility
	{
		public TerraEternalAbility0(GameState state)
		{
			super(state, "All lands are indestructible.");
			this.addEffectPart(indestructible(LandPermanents.instance()));
		}
	}

	public TerraEternal(GameState state)
	{
		super(state);

		// All lands are indestructible.
		this.addAbility(new TerraEternalAbility0(state));
	}
}
