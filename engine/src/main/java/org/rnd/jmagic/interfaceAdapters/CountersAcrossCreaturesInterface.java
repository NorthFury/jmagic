package org.rnd.jmagic.interfaceAdapters;

import org.rnd.jmagic.engine.Counter;
import org.rnd.jmagic.engine.PlayerInterface;
import org.rnd.jmagic.sanitized.SanitizedGameState;
import org.rnd.jmagic.sanitized.SanitizedIdentified;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * An adapter interface for when the game asks a player to choose counters that
 * are on multiple creatures.
 */
public class CountersAcrossCreaturesInterface extends SimplePlayerInterface
{
	public static final ChooseReason REASON = new ChooseReason("CountersAcrossCreaturesInterface", "Choose a permanent to remove counters from.", true);
	private SanitizedGameState lastState = null;

	public CountersAcrossCreaturesInterface(PlayerInterface adapt)
	{
		super(adapt);
	}

	@Override
	public void alertState(SanitizedGameState sanitizedGameState)
	{
		this.lastState = sanitizedGameState;
		super.alertState(sanitizedGameState);
	}

	@Override
	public <T extends Serializable> List<Integer> choose(ChooseParameters<T> parameterObject)
	{
		if(parameterObject.reason != ChooseReason.CHOOSE_COUNTERS)
			return super.choose(parameterObject);

		Map<Integer, List<Counter>> counterMap = new HashMap<Integer, List<Counter>>();
		List<SanitizedIdentified> availablePermanents = new LinkedList<SanitizedIdentified>();
		for(T c: parameterObject.choices)
		{
			Counter counter = (Counter)c;
			if(counterMap.containsKey(counter.sourceID))
				counterMap.get(counter.sourceID).add(counter);
			else
			{
				List<Counter> counters = new ArrayList<Counter>();
				counters.add(counter);
				counterMap.put(counter.sourceID, counters);
				availablePermanents.add(this.lastState.get(counter.sourceID));
			}
		}

		if(counterMap.size() <= 1)
			return super.choose(parameterObject);

		List<Counter> chosenCounters = new LinkedList<Counter>();
		while(availablePermanents.size() > 0)
		{
			ChooseParameters<SanitizedIdentified> choosePermanent = new ChooseParameters<SanitizedIdentified>(0, 1, ChoiceType.OBJECTS, REASON);
			choosePermanent.choices = availablePermanents;
			List<Integer> permanentChoice = this.choose(choosePermanent);
			if(permanentChoice.isEmpty())
				break;

			int index = permanentChoice.get(0);
			SanitizedIdentified permanent = availablePermanents.remove(index);
			ChooseParameters<Counter> chooseCounters = new ChooseParameters<Counter>(0, null, ChoiceType.STRING, ChooseReason.CHOOSE_COUNTERS);
			List<Counter> availableCounters = counterMap.get(permanent.ID);
			chooseCounters.choices = availableCounters;
			List<Integer> counterChoice = this.choose(chooseCounters);
			for(int choice: counterChoice)
				chosenCounters.add(availableCounters.get(choice));
		}

		List<Integer> ret = new LinkedList<Integer>();
		for(int i = 0; i < parameterObject.choices.size(); i++)
		{
			T c = parameterObject.choices.get(i);
			Counter counter = (Counter)c;
			for(Counter chosen: chosenCounters)
			{
				if(counter.compareTo(chosen) == 0)
				{
					chosenCounters.remove(chosen);
					ret.add(i);
					break;
				}
			}
		}

		return ret;
	}
}
