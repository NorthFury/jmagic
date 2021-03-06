package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Manamorphose")
@Types({Type.INSTANT})
@ManaCost("1(R/G)")
@Printings({@Printings.Printed(ex = Expansion.SHADOWMOOR, r = Rarity.COMMON)})
@ColorIdentity({Color.GREEN, Color.RED})
public final class Manamorphose extends Card
{
	public Manamorphose(GameState state)
	{
		super(state);

		// Add two mana in any combination of colors to your mana pool.
		EventFactory mana = new EventFactory(EventType.ADD_MANA, "Add two mana in any combination of colors to your mana pool.");
		mana.parameters.put(EventType.Parameter.SOURCE, This.instance());
		mana.parameters.put(EventType.Parameter.PLAYER, You.instance());
		mana.parameters.put(EventType.Parameter.MANA, Identity.instance(new ManaPool("(WUBRG)(WUBRG)")));
		this.addEffect(mana);

		// Draw a card.
		this.addEffect(drawACard());
	}
}
