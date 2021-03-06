package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Equip;
import org.rnd.jmagic.abilities.keywords.Hexproof;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Mask of Avacyn")
@Types({Type.ARTIFACT})
@SubTypes({SubType.EQUIPMENT})
@ManaCost("2")
@Printings({@Printings.Printed(ex = Expansion.INNISTRAD, r = Rarity.UNCOMMON)})
@ColorIdentity({})
public final class MaskofAvacyn extends Card
{
	public static final class MaskofAvacynAbility0 extends StaticAbility
	{
		public MaskofAvacynAbility0(GameState state)
		{
			super(state, "Equipped creature gets +1/+2 and has hexproof.");

			SetGenerator equipped = EquippedBy.instance(This.instance());

			this.addEffectPart(modifyPowerAndToughness(equipped, +1, +2));
			this.addEffectPart(addAbilityToObject(equipped, Hexproof.class));
		}
	}

	public MaskofAvacyn(GameState state)
	{
		super(state);

		// Equipped creature gets +1/+2 and has hexproof. (It can't be the
		// target of spells or abilities your opponents control.)
		this.addAbility(new MaskofAvacynAbility0(state));

		// Equip (3)
		this.addAbility(new Equip(state, "(3)"));
	}
}
