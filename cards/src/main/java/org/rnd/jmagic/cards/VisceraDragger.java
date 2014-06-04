package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Cycling;
import org.rnd.jmagic.abilities.keywords.Unearth;
import org.rnd.jmagic.engine.*;

@Name("Viscera Dragger")
@Types({Type.CREATURE})
@SubTypes({SubType.ZOMBIE, SubType.OGRE, SubType.WARRIOR})
@ManaCost("3B")
@Printings({@Printings.Printed(ex = Expansion.SHARDS_OF_ALARA, r = Rarity.COMMON)})
@ColorIdentity({Color.BLACK})
public final class VisceraDragger extends Card
{
	public VisceraDragger(GameState state)
	{
		super(state);

		this.setPower(3);
		this.setToughness(3);

		this.addAbility(new Cycling(state, "(2)"));
		this.addAbility(new Unearth(state, "(1)(B)"));
	}
}
