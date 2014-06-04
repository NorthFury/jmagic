package org.rnd.jmagic.abilities.keywords;

import org.rnd.jmagic.engine.*;

import java.util.LinkedList;
import java.util.List;

@Name("Haste")
public final class Haste extends Keyword
{
	public Haste(GameState state)
	{
		super(state, "Haste");
	}

	@Override
	protected List<StaticAbility> createStaticAbilities()
	{
		List<StaticAbility> abilities = new LinkedList<StaticAbility>();
		abilities.add(new HasteStatic(this.state));
		return abilities;
	}

	public static final class HasteStatic extends StaticAbility
	{
		public HasteStatic(GameState state)
		{
			super(state, "This creature can attack or use activated abilities whose cost includes the tap symbol or the untap symbol even if it hasn't been controlled by its controller continuously since the start of his or her most recent turn");
		}
	}

	/** @return True. */
	@Override
	public boolean isHaste()
	{
		return true;
	}
}
