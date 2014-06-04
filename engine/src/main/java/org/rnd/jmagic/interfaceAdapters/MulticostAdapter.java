package org.rnd.jmagic.interfaceAdapters;

import org.rnd.jmagic.engine.PlayerInterface;
import org.rnd.jmagic.sanitized.SanitizedCostCollection;
import org.rnd.util.NumberRange;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Interface adapter for choosing an optional additional cost multiple times
 * (like replicate or multikicker). This is intended to be an "always-on"
 * adapter, like SwingInterface.
 */
public class MulticostAdapter extends SimplePlayerInterface
{
	public MulticostAdapter(PlayerInterface adapt)
	{
		super(adapt);
	}

	@Override
	public <T extends Serializable> List<Integer> choose(ChooseParameters<T> parameterObject)
	{
		if(!parameterObject.reason.equals(ChooseReason.OPTIONAL_ADDITIONAL_COST))
			return super.choose(parameterObject);

		// We separate the costs into two parts. These maps keep track of
		// the relationship between the indices in the two lists. Keys are
		// indices in the new parameter list, values are indices in the old
		// parameter list.
		Map<Integer, Integer> indexMap = new HashMap<Integer, Integer>();
		ChooseParameters<T> newParameters = new ChooseParameters<T>(0, null, ChoiceType.ALTERNATE_COST, parameterObject.reason);

		// In this map, the keys are the costs themselves and the values are
		// the indices of those costs in the old parameter list.
		Map<T, Integer> multicosts = new HashMap<T, Integer>();

		// Set up the two new choice lists.
		boolean chooseSingles = false;
		int oldIndex = 0;
		for(T choice: parameterObject.choices)
		{
			SanitizedCostCollection cost = (SanitizedCostCollection)choice;

			// If it's a one-off cost, add it to the new parameter set.
			if(!cost.allowMultiples)
			{
				indexMap.put(newParameters.choices.size(), oldIndex);

				chooseSingles = true;
				newParameters.choices.add(choice);
			}
			// Otherwise add the choice to the multicosts set.
			else
				multicosts.put(choice, oldIndex);

			oldIndex++;
		}

		List<Integer> ret = new LinkedList<Integer>();

		// If there were any once-only costs to choose, present the choices.
		if(chooseSingles)
		{
			List<Integer> singleChoices = super.choose(newParameters);
			for(int indexInNewList: singleChoices)
				ret.add(indexMap.get(indexInNewList));
		}
		// Ask about each multi-cost individually.
		for(Map.Entry<T, Integer> costEntry: multicosts.entrySet())
		{
			int number = this.chooseNumber(new NumberRange(0, null), "How many times do you want to pay " + costEntry.getKey() + "?");
			int indexInOldList = costEntry.getValue();
			for(int n = 0; n < number; n++)
				ret.add(indexInOldList);
		}

		return ret;
	}
}
