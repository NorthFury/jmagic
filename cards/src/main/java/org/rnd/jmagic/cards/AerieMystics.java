package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.abilities.keywords.Shroud;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Aerie Mystics")
@Types({Type.CREATURE})
@SubTypes({SubType.BIRD, SubType.WIZARD})
@ManaCost("4W")
@Printings({@Printings.Printed(ex = Expansion.CONFLUX, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.BLUE, Color.WHITE, Color.GREEN})
public final class AerieMystics extends Card
{
	public static final class Shroudiness extends ActivatedAbility
	{
		public Shroudiness(GameState state)
		{
			super(state, "(1)(G)(U): Creatures you control gain shroud until end of turn.");

			this.setManaCost(new ManaPool("1GU"));

			this.addEffect(addAbilityUntilEndOfTurn(Intersect.instance(HasType.instance(Type.CREATURE), ControlledBy.instance(You.instance())), Shroud.class, "Creatures you control gain shroud until end of turn."));
		}
	}

	public AerieMystics(GameState state)
	{
		super(state);

		this.setPower(3);
		this.setToughness(3);

		this.addAbility(new Flying(state));

		this.addAbility(new Shroudiness(state));
	}
}
