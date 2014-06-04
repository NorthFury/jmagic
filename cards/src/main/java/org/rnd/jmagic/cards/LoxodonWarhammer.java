package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Equip;
import org.rnd.jmagic.abilities.keywords.Lifelink;
import org.rnd.jmagic.abilities.keywords.Trample;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Loxodon Warhammer")
@Types({Type.ARTIFACT})
@SubTypes({SubType.EQUIPMENT})
@ManaCost("3")
@Printings({@Printings.Printed(ex = Expansion.TENTH_EDITION, r = Rarity.RARE), @Printings.Printed(ex = Expansion.NINTH_EDITION, r = Rarity.RARE), @Printings.Printed(ex = Expansion.MIRRODIN, r = Rarity.UNCOMMON)})
@ColorIdentity({})
public final class LoxodonWarhammer extends Card
{
	public static final class EquipBonuses extends StaticAbility
	{
		public EquipBonuses(GameState state)
		{
			super(state, "Equipped creature gets +3/+0 and has lifelink and trample.");

			SetGenerator equippedCreature = EquippedBy.instance(This.instance());

			this.addEffectPart(modifyPowerAndToughness(equippedCreature, 3, 0));

			this.addEffectPart(addAbilityToObject(equippedCreature, Lifelink.class, Trample.class));
		}
	}

	public LoxodonWarhammer(GameState state)
	{
		super(state);

		this.addAbility(new EquipBonuses(state));
		this.addAbility(new Equip(state, "(3)"));
	}
}
