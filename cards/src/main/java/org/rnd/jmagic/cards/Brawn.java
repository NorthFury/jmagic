package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Trample;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Brawn")
@Types({Type.CREATURE})
@SubTypes({SubType.INCARNATION})
@ManaCost("3G")
@Printings({@Printings.Printed(ex = Expansion.COMMANDER, r = Rarity.UNCOMMON), @Printings.Printed(ex = Expansion.JUDGMENT, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.GREEN})
public final class Brawn extends Card
{
	public static final class BrawnAbility1 extends StaticAbility
	{
		public BrawnAbility1(GameState state)
		{
			super(state, "As long as Brawn is in your graveyard and you control a Forest, creatures you control have trample.");

			SetGenerator youControlForest = Intersect.instance(ControlledBy.instance(You.instance()), HasSubType.instance(SubType.FOREST));
			this.canApply = Both.instance(THIS_IS_IN_A_GRAVEYARD, youControlForest);

			this.addEffectPart(addAbilityToObject(CREATURES_YOU_CONTROL, Trample.class));
		}
	}

	public Brawn(GameState state)
	{
		super(state);

		this.setPower(3);
		this.setToughness(3);

		// Trample
		this.addAbility(new Trample(state));

		// As long as Brawn is in your graveyard and you control a Forest,
		// creatures you control have trample.
		this.addAbility(new BrawnAbility1(state));
	}
}
