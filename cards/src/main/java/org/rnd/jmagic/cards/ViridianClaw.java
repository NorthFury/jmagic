package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Equip;
import org.rnd.jmagic.abilities.keywords.FirstStrike;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Viridian Claw")
@Types({Type.ARTIFACT})
@SubTypes({SubType.EQUIPMENT})
@ManaCost("2")
@Printings({@Printings.Printed(ex = Expansion.MIRRODIN_BESIEGED, r = Rarity.UNCOMMON)})
@ColorIdentity({})
public final class ViridianClaw extends Card
{
	public static final class ViridianClawAbility0 extends StaticAbility
	{
		public ViridianClawAbility0(GameState state)
		{
			super(state, "Equipped creature gets +1/+0 and has first strike.");
			SetGenerator equippedCreature = EquippedBy.instance(This.instance());
			this.addEffectPart(modifyPowerAndToughness(equippedCreature, +1, +0));
			this.addEffectPart(addAbilityToObject(equippedCreature, FirstStrike.class));
		}
	}

	public ViridianClaw(GameState state)
	{
		super(state);

		// Equipped creature gets +1/+0 and has first strike.
		this.addAbility(new ViridianClawAbility0(state));

		// Equip (1)
		this.addAbility(new Equip(state, "(1)"));
	}
}
