package org.rnd.jmagic.cardTemplates;

import org.rnd.jmagic.abilities.TapForMana;
import org.rnd.jmagic.engine.*;

public abstract class ShardsObelisk extends Card
{
	public ShardsObelisk(GameState state, String mana)
	{
		super(state);

		this.addAbility(new TapForMana.Final(state, mana));
	}
}
