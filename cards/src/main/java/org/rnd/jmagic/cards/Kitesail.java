package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.StaticPTChange;
import org.rnd.jmagic.abilities.keywords.Equip;
import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Kitesail")
@Types({Type.ARTIFACT})
@SubTypes({SubType.EQUIPMENT})
@ManaCost("2")
@Printings({@Printings.Printed(ex = Expansion.MAGIC_2013, r = Rarity.UNCOMMON), @Printings.Printed(ex = Expansion.WORLDWAKE, r = Rarity.COMMON)})
@ColorIdentity({})
public final class Kitesail extends Card
{
	public Kitesail(GameState state)
	{
		super(state);

		// Equipped creature gets +1/+0 and has flying.
		SetGenerator equippedCreature = EquippedBy.instance(This.instance());
		this.addAbility(new StaticPTChange(state, equippedCreature, "Equipped creature", +1, +0, Flying.class, false));

		// Equip (2)
		this.addAbility(new Equip(state, "(2)"));
	}
}
