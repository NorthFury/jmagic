package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.DoubleStrike;
import org.rnd.jmagic.abilities.keywords.Exalted;
import org.rnd.jmagic.abilityTemplates.ExaltedBase;
import org.rnd.jmagic.engine.*;

@Name("Rafiq of the Many")
@SuperTypes({SuperType.LEGENDARY})
@Types({Type.CREATURE})
@SubTypes({SubType.KNIGHT, SubType.HUMAN})
@ManaCost("1GWU")
@Printings({@Printings.Printed(ex = Expansion.FTV_LEGENDS, r = Rarity.MYTHIC), @Printings.Printed(ex = Expansion.SHARDS_OF_ALARA, r = Rarity.MYTHIC)})
@ColorIdentity({Color.BLUE, Color.WHITE, Color.GREEN})
public final class RafiqoftheMany extends Card
{

	public static final class ExaltedDoubleStrike extends ExaltedBase
	{
		public ExaltedDoubleStrike(GameState state)
		{
			super(state, "Whenever a creature you control attacks alone, it gains double strike until end of turn.");
			this.addEffect(addAbilityUntilEndOfTurn(this.thatCreature, DoubleStrike.class, "It gains double strike until end of turn."));
		}
	}

	public RafiqoftheMany(GameState state)
	{
		super(state);

		this.setPower(3);
		this.setToughness(3);

		// Exalted
		this.addAbility(new Exalted(state));

		// Whenever a creature you control attacks alone, it gains double strike
		// until end of turn.
		this.addAbility(new ExaltedDoubleStrike(state));
	}
}
