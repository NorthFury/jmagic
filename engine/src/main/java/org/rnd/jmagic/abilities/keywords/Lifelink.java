package org.rnd.jmagic.abilities.keywords;

import org.rnd.jmagic.engine.*;

import java.util.LinkedList;
import java.util.List;

@Name("Lifelink")
public final class Lifelink extends Keyword
{
	public Lifelink(GameState state)
	{
		super(state, "Lifelink");
	}

	@Override
	protected List<StaticAbility> createStaticAbilities()
	{
		List<StaticAbility> ret = new LinkedList<StaticAbility>();
		ret.add(new LifelinkStatic(this.state));
		return ret;
	}

	public static final class LifelinkStatic extends StaticAbility
	{
		public LifelinkStatic(GameState state)
		{
			super(state, "Damage dealt by this creature also causes you to gain that much life.");
		}
	}
}
