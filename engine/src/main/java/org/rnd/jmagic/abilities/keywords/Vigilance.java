package org.rnd.jmagic.abilities.keywords;

import org.rnd.jmagic.engine.*;

import java.util.LinkedList;
import java.util.List;

@Name("Vigilance")
public final class Vigilance extends Keyword
{
	public Vigilance(GameState state)
	{
		super(state, "Vigilance");
	}

	@Override
	protected List<StaticAbility> createStaticAbilities()
	{
		LinkedList<StaticAbility> ret = new LinkedList<StaticAbility>();
		ret.add(new VigilanceStatic(this.state));
		return ret;
	}

	public static final class VigilanceStatic extends StaticAbility
	{
		public VigilanceStatic(GameState state)
		{
			super(state, "Attacking doesn't cause this creature to tap");
		}
	}
}
