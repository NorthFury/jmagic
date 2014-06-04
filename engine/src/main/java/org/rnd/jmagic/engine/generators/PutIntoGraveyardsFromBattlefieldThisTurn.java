package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Resolves to the set of all objects that were put into a graveyard from the
 * battlefield this turn.
 */
public class PutIntoGraveyardsFromBattlefieldThisTurn extends SetGenerator
{
	public static class DeathTracker extends Tracker<Map<Integer, Set<Integer>>>
	{
		private HashMap<Integer, Set<Integer>> IDs = new HashMap<Integer, Set<Integer>>();
		private Map<Integer, Set<Integer>> unmodifiable = Collections.unmodifiableMap(this.IDs);

		@Override
		public DeathTracker clone()
		{
			DeathTracker ret = (DeathTracker)super.clone();
			ret.IDs = new HashMap<Integer, Set<Integer>>();
			for(Map.Entry<Integer, Set<Integer>> entry: this.IDs.entrySet())
				ret.IDs.put(entry.getKey(), new HashSet<Integer>(entry.getValue()));
			ret.unmodifiable = Collections.unmodifiableMap(ret.IDs);
			return ret;
		}

		@Override
		protected Map<Integer, Set<Integer>> getValueInternal()
		{
			return this.unmodifiable;
		}

		@Override
		protected boolean match(GameState state, Event event)
		{
			if(event.type != EventType.MOVE_BATCH)
				return false;

			for(ZoneChange change: event.parametersNow.get(EventType.Parameter.TARGET).evaluate(state, null).getAll(ZoneChange.class))
			{
				if(state.battlefield().ID != change.sourceZoneID)
					continue;

				Zone to = state.get(change.destinationZoneID);
				if(!to.isGraveyard())
					continue;

				return true;
			}

			return false;
		}

		@Override
		protected void reset()
		{
			this.IDs.clear();
		}

		@Override
		protected void update(GameState state, Event event)
		{
			for(ZoneChange change: event.parametersNow.get(EventType.Parameter.TARGET).evaluate(state, null).getAll(ZoneChange.class))
			{
				if(state.battlefield().ID != change.sourceZoneID)
					continue;

				Zone to = state.get(change.destinationZoneID);
				if(!to.isGraveyard())
					continue;

				int player = to.getOwner(state).ID;

				if(!this.IDs.containsKey(player))
					this.IDs.put(player, new HashSet<Integer>());
				this.IDs.get(player).add(change.newObjectID);
			}
		}
	}

	private static final PutIntoGraveyardsFromBattlefieldThisTurn _instance = new PutIntoGraveyardsFromBattlefieldThisTurn();

	public static PutIntoGraveyardsFromBattlefieldThisTurn instance()
	{
		return _instance;
	}

	private PutIntoGraveyardsFromBattlefieldThisTurn()
	{
		// singleton
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		Turn currentTurn = state.currentTurn();
		if(currentTurn == null)
			return Empty.set;

		MagicSet ret = new MagicSet();
		for(Set<Integer> ids: state.getTracker(DeathTracker.class).getValue(state).values())
			for(int ID: ids)
				ret.add(state.get(ID));
		return ret;
	}
}
