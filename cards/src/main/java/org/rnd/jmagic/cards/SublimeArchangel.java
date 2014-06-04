package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Exalted;
import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Sublime Archangel")
@Types({Type.CREATURE})
@SubTypes({SubType.ANGEL})
@ManaCost("2WW")
@Printings({@Printings.Printed(ex = Expansion.MAGIC_2013, r = Rarity.MYTHIC)})
@ColorIdentity({Color.WHITE})
public final class SublimeArchangel extends Card
{
	public static final class SublimeArchangelAbility2 extends StaticAbility
	{
		public SublimeArchangelAbility2(GameState state)
		{
			super(state, "Other creatures you control have exalted.");
			this.addEffectPart(addAbilityToObject(RelativeComplement.instance(CREATURES_YOU_CONTROL, This.instance()), new SimpleAbilityFactory(Exalted.class)));
		}
	}

	public SublimeArchangel(GameState state)
	{
		super(state);

		this.setPower(4);
		this.setToughness(3);

		// Flying
		this.addAbility(new Flying(state));

		// Exalted (Whenever a creature you control attacks alone, that creature
		// gets +1/+1 until end of turn.)
		this.addAbility(new Exalted(state));

		// Other creatures you control have exalted. (If a creature has multiple
		// instances of exalted, each triggers separately.)
		this.addAbility(new SublimeArchangelAbility2(state));
	}
}
