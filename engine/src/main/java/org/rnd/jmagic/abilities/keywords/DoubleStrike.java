package org.rnd.jmagic.abilities.keywords;

import org.rnd.jmagic.engine.*;

import java.util.LinkedList;
import java.util.List;

@Name("Double strike")
public final class DoubleStrike extends Keyword
{
	public DoubleStrike(GameState state)
	{
		super(state, "Double strike");
	}

	@Override
	protected List<StaticAbility> createStaticAbilities()
	{
		LinkedList<StaticAbility> ret = new LinkedList<StaticAbility>();
		ret.add(new DoubleStrikeStatic(this.state));
		return ret;
	}

	public static final class DoubleStrikeStatic extends StaticAbility
	{
		public DoubleStrikeStatic(GameState state)
		{
			super(state, "This creature deals both first-strike and regular combat damage");
		}
	}
}
