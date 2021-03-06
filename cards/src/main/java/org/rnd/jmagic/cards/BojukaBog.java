package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.EntersTheBattlefieldTapped;
import org.rnd.jmagic.abilities.TapForB;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Bojuka Bog")
@Types({Type.LAND})
@Printings({@Printings.Printed(ex = Expansion.COMMANDER, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.WORLDWAKE, r = Rarity.COMMON)})
@ColorIdentity({Color.BLACK})
public final class BojukaBog extends Card
{
	public static final class UncounterableTormodsCrypt extends EventTriggeredAbility
	{
		public UncounterableTormodsCrypt(GameState state)
		{
			super(state, "When Bojuka Bog enters the battlefield, exile all cards from target player's graveyard.");
			this.addPattern(whenThisEntersTheBattlefield());
			Target target = this.addTarget(Players.instance(), "target player");
			this.addEffect(exile(InZone.instance(GraveyardOf.instance(targetedBy(target))), "Exile all cards from target player's graveyard."));
		}
	}

	public BojukaBog(GameState state)
	{
		super(state);

		// Bojuka Bog enters the battlefield tapped.
		this.addAbility(new EntersTheBattlefieldTapped(state, this.getName()));

		// When Bojuka Bog enters the battlefield, exile all cards from target
		// player's graveyard.
		this.addAbility(new UncounterableTormodsCrypt(state));

		// (T): Add (B) to your mana pool.
		this.addAbility(new TapForB(state));
	}
}
