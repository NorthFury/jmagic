package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Exalted;
import org.rnd.jmagic.abilityTemplates.ExaltedBase;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Angelic Benediction")
@Types({Type.ENCHANTMENT})
@ManaCost("3W")
@Printings({@Printings.Printed(ex = Expansion.MAGIC_2013, r = Rarity.UNCOMMON), @Printings.Printed(ex = Expansion.SHARDS_OF_ALARA, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.WHITE})
public final class AngelicBenediction extends Card
{
	public static final class AngelicBenedictionAbility1 extends ExaltedBase
	{
		public AngelicBenedictionAbility1(GameState state)
		{
			super(state, "Whenever a creature you control attacks alone, you may tap target creature.");
			SetGenerator target = targetedBy(this.addTarget(CreaturePermanents.instance(), "target creature"));
			this.addEffect(youMay(tap(target, "Tap target creature"), "You may tap target creature."));
		}
	}

	public AngelicBenediction(GameState state)
	{
		super(state);

		// Exalted (Whenever a creature you control attacks alone, that creature
		// gets +1/+1 until end of turn.)
		this.addAbility(new Exalted(state));

		// Whenever a creature you control attacks alone, you may tap target
		// creature.
		this.addAbility(new AngelicBenedictionAbility1(state));
	}
}
