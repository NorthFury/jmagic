package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.StaticPTChange;
import org.rnd.jmagic.abilities.keywords.Equip;
import org.rnd.jmagic.abilities.keywords.Reach;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Spidersilk Net")
@Types({Type.ARTIFACT})
@SubTypes({SubType.EQUIPMENT})
@ManaCost("0")
@Printings({@Printings.Printed(ex = Expansion.ZENDIKAR, r = Rarity.COMMON)})
@ColorIdentity({})
public final class SpidersilkNet extends Card
{
	public SpidersilkNet(GameState state)
	{
		super(state);

		this.addAbility(new StaticPTChange(state, EquippedBy.instance(This.instance()), "Equipped creature", 0, 2, Reach.class, false));

		this.addAbility(new Equip(state, "(2)"));
	}
}
