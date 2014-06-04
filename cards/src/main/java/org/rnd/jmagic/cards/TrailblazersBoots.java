package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Equip;
import org.rnd.jmagic.abilities.keywords.Landwalk;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Trailblazer's Boots")
@Types({Type.ARTIFACT})
@SubTypes({SubType.EQUIPMENT})
@ManaCost("2")
@Printings({@Printings.Printed(ex = Expansion.ZENDIKAR, r = Rarity.UNCOMMON)})
@ColorIdentity({})
public final class TrailblazersBoots extends Card
{
	public static final class TrailblazersBootsAbility0 extends StaticAbility
	{
		public TrailblazersBootsAbility0(GameState state)
		{
			super(state, "Equipped creature has nonbasic landwalk.");
			this.addEffectPart(addAbilityToObject(EquippedBy.instance(This.instance()), Landwalk.NonbasicLandwalk.class));
		}
	}

	public TrailblazersBoots(GameState state)
	{
		super(state);

		// Equipped creature has nonbasic landwalk.
		this.addAbility(new TrailblazersBootsAbility0(state));

		// Equip (2)
		this.addAbility(new Equip(state, "(2)"));
	}
}
