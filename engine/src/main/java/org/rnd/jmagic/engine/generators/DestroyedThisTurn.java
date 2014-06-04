package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DestroyedThisTurn extends SetGenerator
{
	public static class DestroyedTracker extends Tracker<Map<Integer, Integer>>
	{
		private HashMap<Integer, Integer> IDs = new HashMap<Integer, Integer>();
		private Map<Integer, Integer> unmodifiable = Collections.unmodifiableMap(this.IDs);

		@SuppressWarnings("unchecked")
		@Override
		public DestroyedTracker clone()
		{
			DestroyedTracker ret = (DestroyedTracker)super.clone();
			ret.IDs = (HashMap<Integer, Integer>)this.IDs.clone();
			ret.unmodifiable = Collections.unmodifiableMap(ret.IDs);
			return ret;
		}

		@Override
		protected Map<Integer, Integer> getValueInternal()
		{
			return this.unmodifiable;
		}

		@Override
		protected boolean match(GameState state, Event event)
		{
			return (event.type == EventType.DESTROY_ONE_PERMANENT);
		}

		@Override
		protected void reset()
		{
			this.IDs.clear();
		}

		@Override
		protected void update(GameState state, Event event)
		{
			MagicSet object = OldObjectOf.instance(event.getResultGenerator()).evaluate(state, null);
			Identified cause = event.parameters.get(EventType.Parameter.CAUSE).evaluate(state, event.getSource()).getOne(Identified.class);

			// If the cause is the game, use 0.
			int causeID = (cause == null ? 0 : cause.ID);

			for(GameObject o: object.getAll(GameObject.class))
				this.IDs.put(o.ID, causeID);
		}
	}

	private static DestroyedThisTurn _instance = new DestroyedThisTurn();

	public static DestroyedThisTurn instance()
	{
		return _instance;
	}

	private DestroyedThisTurn()
	{
		// Singleton constructor
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		Set<Integer> ids = state.getTracker(DestroyedTracker.class).getValue(state).keySet();
		MagicSet ret = new MagicSet();

		for(Integer id: ids)
			ret.add(state.get(id));

		return ret;
	}
}
