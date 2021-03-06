package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Equip;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Warlord's Axe")
@Types({Type.ARTIFACT})
@SubTypes({SubType.EQUIPMENT})
@ManaCost("3")
@Printings({@Printings.Printed(ex = Expansion.MAGIC_2011, r = Rarity.UNCOMMON)})
@ColorIdentity({})
public final class WarlordsAxe extends Card
{
	public final static class WarlordsAxeAbility0 extends StaticAbility
	{
		public WarlordsAxeAbility0(GameState state)
		{
			super(state, "Equipped creature gets +3/+1.");
			this.addEffectPart(modifyPowerAndToughness(EquippedBy.instance(This.instance()), +3, +1));
		}
	}

	public WarlordsAxe(GameState state)
	{
		super(state);

		// Equipped creature gets +3/+1.
		this.addAbility(new WarlordsAxeAbility0(state));

		// Equip (4) ((4): Attach to target creature you control. Equip only as
		// a sorcery.)
		this.addAbility(new Equip(state, "(4)"));
	}
}
