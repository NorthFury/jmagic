package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Equip;
import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Cobbled Wings")
@Types({Type.ARTIFACT})
@SubTypes({SubType.EQUIPMENT})
@ManaCost("2")
@Printings({@Printings.Printed(ex = Expansion.INNISTRAD, r = Rarity.COMMON)})
@ColorIdentity({})
public final class CobbledWings extends Card
{
	public static final class CobbledWingsAbility0 extends StaticAbility
	{
		public CobbledWingsAbility0(GameState state)
		{
			super(state, "Equipped creature has flying.");
			this.addEffectPart(addAbilityToObject(EquippedBy.instance(This.instance()), Flying.class));
		}
	}

	public CobbledWings(GameState state)
	{
		super(state);

		// Equipped creature has flying.
		this.addAbility(new CobbledWingsAbility0(state));

		// Equip (1) ((1): Attach to target creature you control. Equip only as
		// a sorcery.)
		this.addAbility(new Equip(state, "(1)"));
	}
}
