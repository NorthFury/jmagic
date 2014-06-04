package org.rnd.jmagic.engine;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class NormalMulliganTracker extends Tracker<Set<Integer>>
{
	private HashSet<Integer> IDs = new HashSet<Integer>();
	private Set<Integer> unmodifiable = Collections.unmodifiableSet(this.IDs);

	@SuppressWarnings("unchecked")
	@Override
	public NormalMulliganTracker clone()
	{
		NormalMulliganTracker ret = (NormalMulliganTracker)super.clone();
		ret.IDs = (HashSet<Integer>)this.IDs.clone();
		ret.unmodifiable = Collections.unmodifiableSet(ret.IDs);
		return ret;
	}

	@Override
	protected Set<Integer> getValueInternal()
	{
		return this.unmodifiable;
	}

	@Override
	protected void reset()
	{
		this.IDs.clear();
	}

	@Override
	protected boolean match(GameState state, Event event)
	{
		return event.type == EventType.MULLIGAN;
	}

	@Override
	protected void update(GameState state, Event event)
	{
		this.IDs.add(event.parametersNow.get(EventType.Parameter.PLAYER).evaluate(state, null).getOne(Player.class).ID);
	}

}
