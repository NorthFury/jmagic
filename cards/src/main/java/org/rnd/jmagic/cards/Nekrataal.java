package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.FirstStrike;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Nekrataal")
@Types({Type.CREATURE})
@SubTypes({SubType.HUMAN, SubType.ASSASSIN})
@ManaCost("2BB")
@Printings({@Printings.Printed(ex = Expansion.TENTH_EDITION, r = Rarity.UNCOMMON), @Printings.Printed(ex = Expansion.NINTH_EDITION, r = Rarity.UNCOMMON), @Printings.Printed(ex = Expansion.EIGHTH_EDITION, r = Rarity.UNCOMMON), @Printings.Printed(ex = Expansion.VISIONS, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.BLACK})
public final class Nekrataal extends Card
{
	public static final class CIPTerror extends EventTriggeredAbility
	{
		public CIPTerror(GameState state)
		{
			super(state, "When Nekrataal enters the battlefield, destroy target nonartifact, nonblack creature. That creature can't be regenerated.");

			this.addPattern(whenThisEntersTheBattlefield());

			Target target = this.addTarget(RelativeComplement.instance(RelativeComplement.instance(CreaturePermanents.instance(), HasType.instance(Type.ARTIFACT)), HasColor.instance(Color.BLACK)), "target nonblack creature");

			this.addEffects(bury(this, targetedBy(target), "Destroy target nonartifact, nonblack creature. That creature can't be regenerated."));
		}
	}

	public Nekrataal(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(1);

		this.addAbility(new FirstStrike(state));
		this.addAbility(new CIPTerror(state));
	}
}
