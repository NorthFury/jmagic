package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Equip;
import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.abilities.keywords.LivingWeapon;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Skinwing")
@Types({Type.ARTIFACT})
@SubTypes({SubType.EQUIPMENT})
@ManaCost("4")
@Printings({@Printings.Printed(ex = Expansion.MIRRODIN_BESIEGED, r = Rarity.UNCOMMON)})
@ColorIdentity({})
public final class Skinwing extends Card
{
	public static final class SkinwingAbility1 extends StaticAbility
	{
		public SkinwingAbility1(GameState state)
		{
			super(state, "Equipped creature gets +2/+2 and has flying.");

			SetGenerator equippedCreature = EquippedBy.instance(This.instance());

			this.addEffectPart(modifyPowerAndToughness(equippedCreature, +2, +2));
			this.addEffectPart(addAbilityToObject(equippedCreature, Flying.class));
		}
	}

	public Skinwing(GameState state)
	{
		super(state);

		// Living weapon (When this Equipment enters the battlefield, put a 0/0
		// black Germ creature token onto the battlefield, then attach this to
		// it.)
		this.addAbility(new LivingWeapon(state));

		// Equipped creature gets +2/+2 and has flying.
		this.addAbility(new SkinwingAbility1(state));

		// Equip (6)
		this.addAbility(new Equip(state, "(6)"));
	}
}
