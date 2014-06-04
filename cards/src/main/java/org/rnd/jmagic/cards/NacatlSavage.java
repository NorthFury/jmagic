package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.Protection;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.HasType;

@Name("Nacatl Savage")
@Types({Type.CREATURE})
@SubTypes({SubType.WARRIOR, SubType.CAT})
@ManaCost("1G")
@Printings({@Printings.Printed(ex = Expansion.CONFLUX, r = Rarity.COMMON)})
@ColorIdentity({Color.GREEN})
public final class NacatlSavage extends Card
{
	public NacatlSavage(GameState state)
	{
		super(state);

		this.setPower(2);
		this.setToughness(1);

		this.addAbility(new Protection.From(state, HasType.instance(Type.ARTIFACT), "artifacts"));
	}
}
