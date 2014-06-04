package org.rnd.jmagic.abilities.keywords;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.LinkedList;
import java.util.List;

@Name("Fear")
public final class Fear extends Keyword
{
	public Fear(GameState state)
	{
		super(state, "Fear");
	}

	@Override
	protected List<StaticAbility> createStaticAbilities()
	{
		LinkedList<StaticAbility> ret = new LinkedList<StaticAbility>();
		ret.add(new FearStatic(this.state));
		return ret;
	}

	public static final class FearStatic extends StaticAbility
	{
		public FearStatic(GameState state)
		{
			super(state, "This creature can't be blocked except by artifact creatures and/or black creatures.");

			SetGenerator artifacts = HasType.instance(Type.ARTIFACT);
			// ACK! This is affectable by text change effects!!
			// TODO : HasColorNoTextChange.instance()? -RulesGuru
			SetGenerator black = HasColor.instance(Color.BLACK);
			SetGenerator creatures = HasType.instance(Type.CREATURE);
			SetGenerator nonBlackNonArtifactCreatures = RelativeComplement.instance(creatures, Union.instance(artifacts, black));
			SetGenerator illegalBlock = Intersect.instance(Blocking.instance(This.instance()), nonBlackNonArtifactCreatures);

			ContinuousEffect.Part part = new ContinuousEffect.Part(ContinuousEffectType.BLOCKING_RESTRICTION);
			part.parameters.put(ContinuousEffectType.Parameter.RESTRICTION, Identity.instance(illegalBlock));
			this.addEffectPart(part);
		}
	}
}
