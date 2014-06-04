package org.rnd.jmagic.abilities.keywords;

import org.rnd.jmagic.engine.*;

import java.util.LinkedList;
import java.util.List;

@Name("Reach")
public final class Reach extends Keyword
{
	public Reach(GameState state)
	{
		super(state, "Reach");
	}

	@Override
	protected List<StaticAbility> createStaticAbilities()
	{
		LinkedList<StaticAbility> ret = new LinkedList<StaticAbility>();
		ret.add(new ReachStatic(this.state));
		return ret;
	}

	public static final class ReachStatic extends StaticAbility
	{
		public ReachStatic(GameState state)
		{
			super(state, "This creature can block creatures with flying");
		}
	}
}
