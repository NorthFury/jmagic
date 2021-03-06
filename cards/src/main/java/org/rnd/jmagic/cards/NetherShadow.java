package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Haste;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Nether Shadow")
@Types({Type.CREATURE})
@SubTypes({SubType.SPIRIT})
@ManaCost("BB")
@Printings({@Printings.Printed(ex = Expansion.FIFTH_EDITION, r = Rarity.RARE), @Printings.Printed(ex = Expansion.FOURTH_EDITION, r = Rarity.RARE), @Printings.Printed(ex = Expansion.REVISED, r = Rarity.RARE), @Printings.Printed(ex = Expansion.UNLIMITED, r = Rarity.RARE), @Printings.Printed(ex = Expansion.BETA, r = Rarity.RARE), @Printings.Printed(ex = Expansion.ALPHA, r = Rarity.RARE)})
@ColorIdentity({Color.BLACK})
public final class NetherShadow extends Card
{
	public static final class NetherShadowAbility1 extends EventTriggeredAbility
	{
		public NetherShadowAbility1(GameState state)
		{
			super(state, "At the beginning of your upkeep, if Nether Shadow is in your graveyard with three or more creature cards above it, you may put Nether Shadow onto the battlefield.");
			this.addPattern(atTheBeginningOfYourUpkeep());

			SetGenerator cardsAboveThis = TopCards.instance(Subtract.instance(IndexOf.instance(ABILITY_SOURCE_OF_THIS), numberGenerator(0)), LibraryOf.instance(You.instance()));
			this.interveningIf = Both.instance(ABILITY_SOURCE_IS_IN_GRAVEYARD, Intersect.instance(Count.instance(Intersect.instance(cardsAboveThis, HasType.instance(Type.CREATURE))), Between.instance(3, null)));
			this.canTrigger = this.interveningIf;

			this.addEffect(youMay(putOntoBattlefield(ABILITY_SOURCE_OF_THIS, "Put Nether Shadow onto the battlefield."), "You may put Nether Shadow onto the battlefield."));
		}
	}

	public NetherShadow(GameState state)
	{
		super(state);

		this.setPower(1);
		this.setToughness(1);

		// Haste
		this.addAbility(new Haste(state));

		// At the beginning of your upkeep, if Nether Shadow is in your
		// graveyard with three or more creature cards above it, you may put
		// Nether Shadow onto the battlefield.
		this.addAbility(new NetherShadowAbility1(state));
	}
}
