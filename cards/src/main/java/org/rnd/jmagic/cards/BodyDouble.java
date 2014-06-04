package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.YouMayHaveThisEnterTheBattlefieldAsACopy;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Body Double")
@Types({Type.CREATURE})
@SubTypes({SubType.SHAPESHIFTER})
@ManaCost("4U")
@Printings({@Printings.Printed(ex = Expansion.PLANAR_CHAOS, r = Rarity.RARE)})
@ColorIdentity({Color.BLUE})
public final class BodyDouble extends Card
{
	public BodyDouble(GameState state)
	{
		super(state);

		this.setPower(0);
		this.setToughness(0);

		// You may have Body Double enter the battlefield as a copy of any
		// creature card in a graveyard.
		SetGenerator deadGuys = Intersect.instance(HasType.instance(Type.CREATURE), InZone.instance(GraveyardOf.instance(Players.instance())));

		YouMayHaveThisEnterTheBattlefieldAsACopy etb = new YouMayHaveThisEnterTheBattlefieldAsACopy(deadGuys);
		etb.generateName(this.getName(), "any creature card in a graveyard");
		this.addAbility(etb.getStaticAbility(state));
	}
}
