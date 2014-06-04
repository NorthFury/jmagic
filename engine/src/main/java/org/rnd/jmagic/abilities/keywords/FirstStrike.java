package org.rnd.jmagic.abilities.keywords;

import org.rnd.jmagic.engine.*;

import java.util.LinkedList;
import java.util.List;

@Name("First strike")
public final class FirstStrike extends Keyword
{
	public FirstStrike(GameState state)
	{
		super(state, "First strike");
	}

	@Override
	protected List<StaticAbility> createStaticAbilities()
	{
		LinkedList<StaticAbility> ret = new LinkedList<StaticAbility>();
		ret.add(new FirstStrikeStatic(this.state));
		return ret;
	}

	public static final class FirstStrikeStatic extends StaticAbility
	{
		public FirstStrikeStatic(GameState state)
		{
			super(state, "This creature deals combat damage before creatures without first strike");
		}
	}
}
