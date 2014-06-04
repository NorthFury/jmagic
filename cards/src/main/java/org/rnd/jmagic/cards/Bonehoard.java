package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Equip;
import org.rnd.jmagic.abilities.keywords.LivingWeapon;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Bonehoard")
@Types({Type.ARTIFACT})
@SubTypes({SubType.EQUIPMENT})
@ManaCost("4")
@Printings({@Printings.Printed(ex = Expansion.MIRRODIN_BESIEGED, r = Rarity.RARE)})
@ColorIdentity({})
public final class Bonehoard extends Card
{
	public static final class BonehoardAbility1 extends StaticAbility
	{
		public BonehoardAbility1(GameState state)
		{
			super(state, "Equipped creature gets +X/+X, where X is the number of creature cards in all graveyards.");

			SetGenerator number = Count.instance(Intersect.instance(InZone.instance(GraveyardOf.instance(Players.instance())), HasType.instance(Type.CREATURE)));

			this.addEffectPart(modifyPowerAndToughness(EquippedBy.instance(This.instance()), number, number));
		}
	}

	public Bonehoard(GameState state)
	{
		super(state);

		// Living weapon (When this Equipment enters the battlefield, put a 0/0
		// black Germ creature token onto the battlefield, then attach this to
		// it.)
		this.addAbility(new LivingWeapon(state));

		// Equipped creature gets +X/+X, where X is the number of creature cards
		// in all graveyards.
		this.addAbility(new BonehoardAbility1(state));

		// Equip (2)
		this.addAbility(new Equip(state, "(2)"));
	}
}
