package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.CantBlock;
import org.rnd.jmagic.abilities.ZendikarAllyCounter;
import org.rnd.jmagic.engine.*;

@Name("Bojuka Brigand")
@Types({Type.CREATURE})
@SubTypes({SubType.HUMAN, SubType.ALLY, SubType.WARRIOR})
@ManaCost("1B")
@Printings({@Printings.Printed(ex = Expansion.WORLDWAKE, r = Rarity.COMMON)})
@ColorIdentity({Color.BLACK})
public final class BojukaBrigand extends Card
{
	public BojukaBrigand(GameState state)
	{
		super(state);

		this.setPower(1);
		this.setToughness(1);

		// Bojuka Brigand can't block.
		this.addAbility(new CantBlock(state, this.getName()));

		// Whenever Bojuka Brigand or another Ally enters the battlefield under
		// your control, you may put a +1/+1 counter on Bojuka Brigand.
		this.addAbility(new ZendikarAllyCounter(state, this.getName()));
	}
}
