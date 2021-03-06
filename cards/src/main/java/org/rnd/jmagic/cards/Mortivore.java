package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.Regenerate;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Mortivore")
@Types({Type.CREATURE})
@SubTypes({SubType.LHURGOYF})
@ManaCost("2BB")
@Printings({@Printings.Printed(ex = Expansion.COMMANDER, r = Rarity.RARE), @Printings.Printed(ex = Expansion.TENTH_EDITION, r = Rarity.RARE), @Printings.Printed(ex = Expansion.NINTH_EDITION, r = Rarity.RARE), @Printings.Printed(ex = Expansion.ODYSSEY, r = Rarity.RARE)})
@ColorIdentity({Color.BLACK})
public final class Mortivore extends Card
{
	public static final class MortivoreCDA extends CharacteristicDefiningAbility
	{
		public MortivoreCDA(GameState state)
		{
			super(state, "Mortivore's power and toughness are each equal to the number of creature cards in all graveyards.", Characteristics.Characteristic.POWER, Characteristics.Characteristic.TOUGHNESS);

			SetGenerator cardsInGraveyards = InZone.instance(GraveyardOf.instance(Players.instance()));
			SetGenerator creaturesInGraveyards = Count.instance(Intersect.instance(cardsInGraveyards, HasType.instance(Type.CREATURE)));

			this.addEffectPart(setPowerAndToughness(This.instance(), creaturesInGraveyards, creaturesInGraveyards));
		}
	}

	public Mortivore(GameState state)
	{
		super(state);

		this.setPower(0);
		this.setToughness(0);

		this.addAbility(new MortivoreCDA(state));
		this.addAbility(new Regenerate.Final(state, "(B)", this.getName()));
	}
}
