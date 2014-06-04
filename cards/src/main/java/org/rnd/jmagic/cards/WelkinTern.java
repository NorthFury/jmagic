package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Welkin Tern")
@Types({Type.CREATURE})
@SubTypes({SubType.BIRD})
@ManaCost("1U")
@Printings({@Printings.Printed(ex = Expansion.MAGIC_2013, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.ZENDIKAR, r = Rarity.COMMON)})
@ColorIdentity({Color.BLUE})
public final class WelkinTern extends Card
{
	public static final class HighFlying extends StaticAbility
	{
		public HighFlying(GameState state)
		{
			super(state, "Welkin Tern can block only creatures with flying.");

			SetGenerator blockedByThis = BlockedBy.instance(This.instance());
			SetGenerator withFlying = HasKeywordAbility.instance(Flying.class);
			SetGenerator restriction = RelativeComplement.instance(blockedByThis, withFlying);

			ContinuousEffect.Part part = new ContinuousEffect.Part(ContinuousEffectType.BLOCKING_RESTRICTION);
			part.parameters.put(ContinuousEffectType.Parameter.RESTRICTION, Identity.instance(restriction));
			this.addEffectPart(part);
		}
	}

	public WelkinTern(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(1);

		// Flying
		this.addAbility(new Flying(state));

		// Welkin Tern can block only creatures with flying.
		this.addAbility(new HighFlying(state));
	}
}
