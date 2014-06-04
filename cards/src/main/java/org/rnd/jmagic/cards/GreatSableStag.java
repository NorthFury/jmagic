package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.CantBeCountered;
import org.rnd.jmagic.abilities.keywords.Protection;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.HasColor;

@Name("Great Sable Stag")
@Types({Type.CREATURE})
@SubTypes({SubType.ELK})
@ManaCost("1GG")
@Printings({@Printings.Printed(ex = Expansion.MAGIC_2010, r = Rarity.RARE)})
@ColorIdentity({Color.GREEN})
public final class GreatSableStag extends Card
{
	public GreatSableStag(GameState state)
	{
		super(state);

		this.setPower(3);
		this.setToughness(3);

		// Great Sable Stag can't be countered.
		this.addAbility(new CantBeCountered(state, this.getName()));

		// Protection from blue and from black (This creature can't be blocked,
		// targeted, dealt damage, or enchanted by anything blue or black.)
		this.addAbility(new Protection.From(state, HasColor.instance(Color.BLUE, Color.BLACK), "blue and from black"));
	}
}
