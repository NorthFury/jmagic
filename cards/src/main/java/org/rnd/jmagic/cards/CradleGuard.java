package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Echo;
import org.rnd.jmagic.abilities.keywords.Trample;
import org.rnd.jmagic.engine.*;

@Name("Cradle Guard")
@Types({Type.CREATURE})
@SubTypes({SubType.TREEFOLK})
@ManaCost("1GG")
@Printings({@Printings.Printed(ex = Expansion.URZAS_SAGA, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.GREEN})
public final class CradleGuard extends Card
{
	public CradleGuard(GameState state)
	{
		super(state);

		this.setPower(4);
		this.setToughness(4);

		// Trample
		this.addAbility(new Trample(state));

		// Echo (1)(G)(G) (At the beginning of your upkeep, if this came under
		// your control since the beginning of your last upkeep, sacrifice it
		// unless you pay its echo cost.)
		this.addAbility(new Echo(state, "(1)(G)(G)"));
	}
}
