package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.StaticPTChange;
import org.rnd.jmagic.abilities.keywords.Deathtouch;
import org.rnd.jmagic.abilities.keywords.Equip;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Gorgon Flail")
@Types({Type.ARTIFACT})
@SubTypes({SubType.EQUIPMENT})
@ManaCost("2")
@Printings({@Printings.Printed(ex = Expansion.MAGIC_2010, r = Rarity.UNCOMMON)})
@ColorIdentity({})
public final class GorgonFlail extends Card
{
	public GorgonFlail(GameState state)
	{
		super(state);

		// Equipped creature gets +1/+1 and has deathtouch. (Creatures dealt
		// damage by this creature are destroyed. You can divide its combat
		// damage among any of the creatures blocking or blocked by it.)
		SetGenerator equippedCreature = EquippedBy.instance(This.instance());
		this.addAbility(new StaticPTChange(state, equippedCreature, "Equipped creature", +1, +1, Deathtouch.class, false));

		// Equip {2} ({2}: Attach to target creature you control. Equip only as
		// a sorcery.)
		this.addAbility(new Equip(state, "(2)"));
	}
}
