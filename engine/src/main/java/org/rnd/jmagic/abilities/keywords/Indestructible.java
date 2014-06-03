package org.rnd.jmagic.abilities.keywords;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;
import org.rnd.jmagic.engine.patterns.*;

/**
 * A {@link org.rnd.jmagic.engine.Keyword} that prohibits destroying its source
 * permanent.
 */
public class Indestructible extends Keyword
{
	public Indestructible(GameState state)
	{
		super(state, "Indestructible");
	}

	@Override
	protected java.util.List<StaticAbility> createStaticAbilities()
	{
		java.util.List<StaticAbility> ret = new java.util.LinkedList<StaticAbility>();

		ret.add(new IndestructibleAbility(this.state));

		return ret;
	}

	public static final class IndestructibleAbility extends StaticAbility
	{
		public IndestructibleAbility(GameState state)
		{
			super(state, "This can't be destroyed.");

			SimpleEventPattern destroy = new SimpleEventPattern(EventType.DESTROY_ONE_PERMANENT);
			destroy.put(EventType.Parameter.PERMANENT, This.instance());

			ContinuousEffect.Part part = new ContinuousEffect.Part(ContinuousEffectType.PROHIBIT);
			part.parameters.put(ContinuousEffectType.Parameter.PROHIBITION, Identity.instance(destroy));
			this.addEffectPart(part);
		}
	}
}
