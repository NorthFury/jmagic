package org.rnd.jmagic.abilities.keywords;

import org.rnd.jmagic.engine.*;

import java.util.LinkedList;
import java.util.List;

@Name("Wither")
public final class Wither extends Keyword
{
	public Wither(GameState state)
	{
		super(state, "Wither");
	}

	@Override
	protected List<StaticAbility> createStaticAbilities()
	{
		LinkedList<StaticAbility> ret = new LinkedList<StaticAbility>();
		ret.add(new WitherStatic(this.state));
		return ret;
	}

	public static final class WitherStatic extends StaticAbility
	{
		public WitherStatic(GameState state)
		{
			super(state, "This deals damage to creatures in the form of -1/-1 counters");
		}
	}
}
