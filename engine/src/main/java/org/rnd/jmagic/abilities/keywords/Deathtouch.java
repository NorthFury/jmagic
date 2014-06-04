package org.rnd.jmagic.abilities.keywords;

import org.rnd.jmagic.engine.*;

import java.util.LinkedList;
import java.util.List;

@Name("Deathtouch")
public final class Deathtouch extends Keyword
{
	public Deathtouch(GameState state)
	{
		super(state, "Deathtouch");
	}

	@Override
	protected List<StaticAbility> createStaticAbilities()
	{
		LinkedList<StaticAbility> ret = new LinkedList<StaticAbility>();
		ret.add(new DeathtouchStatic(this.state));
		return ret;
	}

	public static final class DeathtouchStatic extends StaticAbility
	{
		public DeathtouchStatic(GameState state)
		{
			super(state, "Any amount of damage this deals to a creature is enough to destroy it.");
		}
	}
}
