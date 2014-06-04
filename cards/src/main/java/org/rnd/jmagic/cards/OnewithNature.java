package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Enchant;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("One with Nature")
@Types({Type.ENCHANTMENT})
@SubTypes({SubType.AURA})
@ManaCost("G")
@Printings({@Printings.Printed(ex = Expansion.SCOURGE, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.GREEN})
public final class OnewithNature extends Card
{
	public static final class OnewithFetches extends EventTriggeredAbility
	{
		public OnewithFetches(GameState state)
		{
			super(state, "Whenever enchanted creature deals combat damage to a player, you may search your library for a basic land card, put that card onto the battlefield tapped, then shuffle your library.");

			this.addPattern(whenDealsCombatDamageToAPlayer(EnchantedBy.instance(ABILITY_SOURCE_OF_THIS)));

			this.addEffect(youMay(searchYourLibraryForABasicLandCardAndPutItOntoTheBattlefield(true), "You may search your library for a basic land card, put that card onto the battlefield tapped, then shuffle your library."));
		}
	}

	public OnewithNature(GameState state)
	{
		super(state);

		// Enchant creature
		this.addAbility(new Enchant.Creature(state));

		// Whenever enchanted creature deals combat damage to a player, you may
		// search your library for a basic land card, put that card onto the
		// battlefield tapped, then shuffle your library.
		this.addAbility(new OnewithFetches(state));
	}
}
