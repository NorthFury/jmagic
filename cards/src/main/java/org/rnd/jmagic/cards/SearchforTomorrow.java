package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Suspend;
import org.rnd.jmagic.engine.*;

@Name("Search for Tomorrow")
@Types({Type.SORCERY})
@ManaCost("2G")
@Printings({@Printings.Printed(ex = Expansion.TIME_SPIRAL, r = Rarity.COMMON)})
@ColorIdentity({Color.GREEN})
public final class SearchforTomorrow extends Card
{
	public SearchforTomorrow(GameState state)
	{
		super(state);

		this.addEffect(searchYourLibraryForABasicLandCardAndPutItOntoTheBattlefield(false));

		this.addAbility(new Suspend(state, 2, "(G)"));
	}
}
