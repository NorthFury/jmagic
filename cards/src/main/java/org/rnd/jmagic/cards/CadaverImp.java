package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.Gravedigging;
import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.engine.*;

@Name("Cadaver Imp")
@Types({Type.CREATURE})
@SubTypes({SubType.IMP})
@ManaCost("1BB")
@Printings({@Printings.Printed(ex = Expansion.RISE_OF_THE_ELDRAZI, r = Rarity.COMMON)})
@ColorIdentity({Color.BLACK})
public final class CadaverImp extends Card
{

	public CadaverImp(GameState state)
	{
		super(state);

		this.setPower(1);
		this.setToughness(1);

		// Flying
		this.addAbility(new Flying(state));

		// When Cadaver Imp enters the battlefield, you may return target
		// creature card from your graveyard to your hand.
		this.addAbility(new Gravedigging(state, "Cadaver Imp"));
	}
}
