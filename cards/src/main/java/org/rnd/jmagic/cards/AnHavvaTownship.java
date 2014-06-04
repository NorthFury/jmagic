package org.rnd.jmagic.cards;

import org.rnd.jmagic.abilities.TapFor1;
import org.rnd.jmagic.abilities.TapForMana;
import org.rnd.jmagic.engine.*;

@Name("An-Havva Township")
@Types({Type.LAND})
@Printings({@Printings.Printed(ex = Expansion.HOMELANDS, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.WHITE, Color.GREEN, Color.RED})
public final class AnHavvaTownship extends Card
{
	public static final class TapForG extends TapForMana
	{
		public TapForG(GameState state)
		{
			super(state, "(G)");
			this.setManaCost(new ManaPool("1"));
			this.setName("(1), " + this.getName());
		}
	}

	public static final class TapForROrW extends TapForMana
	{
		public TapForROrW(GameState state)
		{
			super(state, "(RW)");
			this.setManaCost(new ManaPool("2"));
			this.setName("(2), " + this.getName());
		}
	}

	public AnHavvaTownship(GameState state)
	{
		super(state);

		this.addAbility(new TapForG(state));
		this.addAbility(new TapForROrW(state));
		this.addAbility(new TapFor1(state));
	}
}
