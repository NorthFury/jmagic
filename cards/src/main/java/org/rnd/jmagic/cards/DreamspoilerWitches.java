package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.abilityTemplates.WhenYouCastASpellDuringOpponentsTurn;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Dreamspoiler Witches")
@Types({Type.CREATURE})
@SubTypes({SubType.FAERIE, SubType.WIZARD})
@ManaCost("3B")
@Printings({@Printings.Printed(ex = Expansion.LORWYN, r = Rarity.COMMON)})
@ColorIdentity({Color.BLACK})
public final class DreamspoilerWitches extends Card
{
	public static final class TrickyMinuses extends WhenYouCastASpellDuringOpponentsTurn
	{
		public TrickyMinuses(GameState state)
		{
			super(state, "you may have target creature get -1/-1 until end of turn.");
			Target target = this.addTarget(CreaturePermanents.instance(), "target creature");

			EventFactory minus = ptChangeUntilEndOfTurn(targetedBy(target), -1, -1, "Target creature gets -1/-1 until end of turn");
			this.addEffect(youMay(minus, "You may have target creature get -1/-1 until end of turn."));
		}
	}

	public DreamspoilerWitches(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(2);

		// Flying
		this.addAbility(new Flying(state));

		// Whenever you cast a spell during an opponent's turn, you may have
		// target creature get -1/-1 until end of turn.
		this.addAbility(new TrickyMinuses(state));
	}
}
