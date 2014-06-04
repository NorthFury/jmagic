package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Equip;
import org.rnd.jmagic.abilities.keywords.Haste;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Strider Harness")
@Types({Type.ARTIFACT})
@SubTypes({SubType.EQUIPMENT})
@ManaCost("3")
@Printings({@Printings.Printed(ex = Expansion.SCARS_OF_MIRRODIN, r = Rarity.COMMON)})
@ColorIdentity({})
public final class StriderHarness extends Card
{
	public static final class StriderHarnessAbility0 extends StaticAbility
	{
		public StriderHarnessAbility0(GameState state)
		{
			super(state, "Equipped creature gets +1/+1 and has haste.");

			SetGenerator equipped = EquippedBy.instance(This.instance());

			this.addEffectPart(modifyPowerAndToughness(equipped, +1, +1));
			this.addEffectPart(addAbilityToObject(equipped, Haste.class));
		}
	}

	public StriderHarness(GameState state)
	{
		super(state);

		// Equipped creature gets +1/+1 and has haste.
		this.addAbility(new StriderHarnessAbility0(state));

		// Equip (1)
		this.addAbility(new Equip(state, "(1)"));
	}
}
