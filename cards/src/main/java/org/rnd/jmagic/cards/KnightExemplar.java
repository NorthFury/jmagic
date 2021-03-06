package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.FirstStrike;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Knight Exemplar")
@Types({Type.CREATURE})
@SubTypes({SubType.HUMAN, SubType.KNIGHT})
@ManaCost("1WW")
@Printings({@Printings.Printed(ex = Expansion.MAGIC_2011, r = Rarity.RARE)})
@ColorIdentity({Color.WHITE})
public final class KnightExemplar extends Card
{
	public static final class LordExemplar extends StaticAbility
	{
		public LordExemplar(GameState state)
		{
			super(state, "Other Knight creatures you control get +1/+1 and are indestructible.");

			SetGenerator otherKnights = RelativeComplement.instance(Intersect.instance(CREATURES_YOU_CONTROL, HasSubType.instance(SubType.KNIGHT)), This.instance());

			this.addEffectPart(modifyPowerAndToughness(otherKnights, 1, 1));
			this.addEffectPart(indestructible(otherKnights));
		}
	}

	public KnightExemplar(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(2);

		// First strike (This creature deals combat damage before creatures
		// without first strike.)
		this.addAbility(new FirstStrike(state));

		// Other Knight creatures you control get +1/+1 and are indestructible.
		// (Lethal damage and effects that say "destroy" don't destroy them.)
		this.addAbility(new LordExemplar(state));
	}
}
