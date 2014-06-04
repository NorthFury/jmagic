package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.abilities.keywords.Indestructible;
import org.rnd.jmagic.engine.*;

@Name("Darksteel Gargoyle")
@Types({Type.ARTIFACT, Type.CREATURE})
@SubTypes({SubType.GARGOYLE})
@ManaCost("7")
@Printings({@Printings.Printed(ex = Expansion.DARKSTEEL, r = Rarity.UNCOMMON)})
@ColorIdentity({})
public final class DarksteelGargoyle extends Card
{
	public DarksteelGargoyle(GameState state)
	{
		super(state);

		this.setPower(3);
		this.setToughness(3);

		this.addAbility(new Flying(state));
		this.addAbility(new Indestructible(state));
	}
}
