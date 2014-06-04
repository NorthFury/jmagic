package org.rnd.jmagic.abilities.keywords;

import org.rnd.jmagic.engine.*;

import java.util.LinkedList;
import java.util.List;

@Name("Infect")
public final class Infect extends Keyword
{
	public Infect(GameState state)
	{
		super(state, "Infect");
	}

	@Override
	protected List<StaticAbility> createStaticAbilities()
	{
		LinkedList<StaticAbility> ret = new LinkedList<StaticAbility>();
		ret.add(new InfectStatic(this.state));
		return ret;
	}

	public static final class InfectStatic extends StaticAbility
	{
		public InfectStatic(GameState state)
		{
			super(state, "This creature deals damage to creatures in the form of -1/-1 counters and to players in the form of poison counters");
		}
	}
}
