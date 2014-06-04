package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.keywords.FirstStrike;
import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.abilities.keywords.Lifelink;
import org.rnd.jmagic.abilities.keywords.Protection;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Baneslayer Angel")
@Types({Type.CREATURE})
@SubTypes({SubType.ANGEL})
@ManaCost("3WW")
@Printings({@Printings.Printed(ex = Expansion.MAGIC_2011, r = Rarity.MYTHIC), @Printings.Printed(ex = Expansion.MAGIC_2010, r = Rarity.MYTHIC)})
@ColorIdentity({Color.WHITE})
public final class BaneslayerAngel extends Card
{
	public BaneslayerAngel(GameState state)
	{
		super(state);

		this.setPower(5);
		this.setToughness(5);

		this.addAbility(new Flying(state));
		this.addAbility(new FirstStrike(state));
		this.addAbility(new Lifelink(state));
		this.addAbility(new Protection.From(state, Union.instance(HasSubType.instance(SubType.DEMON), HasSubType.instance(SubType.DRAGON)), "Demons and from Dragons"));
	}
}
