package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Equip;
import org.rnd.jmagic.abilities.keywords.Lifelink;
import org.rnd.jmagic.abilities.keywords.Trample;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Behemoth Sledge")
@Types({Type.ARTIFACT})
@SubTypes({SubType.EQUIPMENT})
@ManaCost("1GW")
@Printings({@Printings.Printed(ex = Expansion.ALARA_REBORN, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.WHITE, Color.GREEN})
public final class BehemothSledge extends Card
{
	public static final class EquipBonuses extends StaticAbility
	{
		public EquipBonuses(GameState state)
		{
			super(state, "Equipped creature gets +2/+2 and has lifelink and trample.");

			SetGenerator equippedCreature = EquippedBy.instance(This.instance());

			this.addEffectPart(modifyPowerAndToughness(equippedCreature, 2, 2));

			this.addEffectPart(addAbilityToObject(equippedCreature, Lifelink.class, Trample.class));
		}
	}

	public BehemothSledge(GameState state)
	{
		super(state);

		this.addAbility(new EquipBonuses(state));
		this.addAbility(new Equip(state, "(3)"));
	}
}
