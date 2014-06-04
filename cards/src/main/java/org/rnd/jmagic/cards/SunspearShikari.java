package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.FirstStrike;
import org.rnd.jmagic.abilities.keywords.Lifelink;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Sunspear Shikari")
@Types({Type.CREATURE})
@SubTypes({SubType.SOLDIER, SubType.CAT})
@ManaCost("1W")
@Printings({@Printings.Printed(ex = Expansion.SCARS_OF_MIRRODIN, r = Rarity.COMMON)})
@ColorIdentity({Color.WHITE})
public final class SunspearShikari extends Card
{
	public static final class SunspearShikariAbility0 extends StaticAbility
	{
		public SunspearShikariAbility0(GameState state)
		{
			super(state, "As long as Sunspear Shikari is equipped, it has first strike and lifelink.");

			this.canApply = Both.instance(this.canApply, Intersect.instance(HasSubType.instance(SubType.EQUIPMENT), AttachedTo.instance(This.instance())));

			this.addEffectPart(addAbilityToObject(This.instance(), FirstStrike.class, Lifelink.class));
		}
	}

	public SunspearShikari(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(2);

		// As long as Sunspear Shikari is equipped, it has first strike and
		// lifelink.
		this.addAbility(new SunspearShikariAbility0(state));
	}
}
