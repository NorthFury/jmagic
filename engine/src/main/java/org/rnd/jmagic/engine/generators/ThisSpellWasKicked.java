package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

public class ThisSpellWasKicked extends SetGenerator
{
	public static ThisSpellWasKicked instance(CostCollection cost)
	{
		return new ThisSpellWasKicked(cost);
	}

	private CostCollection cost;

	private ThisSpellWasKicked(CostCollection cost)
	{
		this.cost = cost;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		if(!(thisObject.isGameObject()))
			return Empty.set;
		if(thisObject.isActivatedAbility() || thisObject.isTriggeredAbility())
			thisObject = ((NonStaticAbility)thisObject).getSource(state);

		int timesKicked = 0;
		for(CostCollection cost: ((GameObject)thisObject).getOptionalAdditionalCostsChosen())
			if(this.cost.equals(cost))
				timesKicked++;

		if(timesKicked == 0)
			return Empty.set;
		return new MagicSet.Unmodifiable(timesKicked);
	}
}
