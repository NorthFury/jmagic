package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.StaticPTChange;
import org.rnd.jmagic.abilities.keywords.Equip;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Leonin Scimitar")
@Types({Type.ARTIFACT})
@SubTypes({SubType.EQUIPMENT})
@ManaCost("1")
@Printings({@Printings.Printed(ex = Expansion.TENTH_EDITION, r = Rarity.UNCOMMON), @Printings.Printed(ex = Expansion.MIRRODIN, r = Rarity.COMMON)})
@ColorIdentity({})
public final class LeoninScimitar extends Card
{
	public LeoninScimitar(GameState state)
	{
		super(state);

		this.addAbility(new Equip(state, "(1)"));

		SetGenerator enchantedCreature = EquippedBy.instance(This.instance());
		this.addAbility(new StaticPTChange(state, enchantedCreature, "Equipped creature", +1, +1, false));
	}
}
