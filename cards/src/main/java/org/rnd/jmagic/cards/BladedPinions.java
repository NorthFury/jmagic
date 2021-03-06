package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Equip;
import org.rnd.jmagic.abilities.keywords.FirstStrike;
import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Bladed Pinions")
@Types({Type.ARTIFACT})
@SubTypes({SubType.EQUIPMENT})
@ManaCost("2")
@Printings({@Printings.Printed(ex = Expansion.SCARS_OF_MIRRODIN, r = Rarity.COMMON)})
@ColorIdentity({})
public final class BladedPinions extends Card
{
	public static final class BladedPinionsAbility0 extends StaticAbility
	{
		public BladedPinionsAbility0(GameState state)
		{
			super(state, "Equipped creature has flying and first strike.");

			this.addEffectPart(addAbilityToObject(EquippedBy.instance(This.instance()), Flying.class, FirstStrike.class));
		}
	}

	public BladedPinions(GameState state)
	{
		super(state);

		// Equipped creature has flying and first strike.
		this.addAbility(new BladedPinionsAbility0(state));

		// Equip (2)
		this.addAbility(new Equip(state, "(2)"));
	}
}
