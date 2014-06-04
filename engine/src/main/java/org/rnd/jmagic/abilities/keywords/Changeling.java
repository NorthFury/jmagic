package org.rnd.jmagic.abilities.keywords;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.LinkedList;
import java.util.List;

public final class Changeling extends Keyword
{
	public Changeling(GameState state)
	{
		super(state, "Changeling");
	}

	@Override
	protected List<StaticAbility> createStaticAbilities()
	{
		LinkedList<StaticAbility> ret = new LinkedList<StaticAbility>();
		ret.add(new ChangelingAbility(this.state));
		return ret;
	}

	public static final class ChangelingAbility extends CharacteristicDefiningAbility
	{
		public ChangelingAbility(GameState state)
		{
			super(state, "This object is every creature type.", Characteristics.Characteristic.TYPES);

			ContinuousEffect.Part part = new ContinuousEffect.Part(ContinuousEffectType.ADD_TYPES);
			part.parameters.put(ContinuousEffectType.Parameter.OBJECT, This.instance());
			part.parameters.put(ContinuousEffectType.Parameter.TYPE, Identity.instance(SubType.getAllCreatureTypes()));
			this.addEffectPart(part);
		}
	}
}
