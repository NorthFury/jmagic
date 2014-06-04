package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Evaluates to the maximum number of objects some movement happened to this
 * turn under a given player's control. The movement being tracked is defined by
 * the class of tracker passed to the instance method.
 */
public class MaximumPerPlayer extends SetGenerator
{
	/**
	 * Track the number of {@link GameObject} instances in the result of any
	 * matching event. Keys are controller IDs, values are the number of
	 * GameObject instances controlled by that player (or owned if those objects
	 * are not on the battlefield or the stack) in the result of any matches
	 * this turn.
	 */
	public abstract static class GameObjectsThisTurnCounter extends Tracker<Map<Integer, Integer>>
	{
		protected HashMap<Integer, Integer> counts = new HashMap<Integer, Integer>();
		private Map<Integer, Integer> unmodifiable = Collections.unmodifiableMap(this.counts);

		@SuppressWarnings("unchecked")
		@Override
		public GameObjectsThisTurnCounter clone()
		{
			GameObjectsThisTurnCounter ret = (GameObjectsThisTurnCounter)super.clone();
			ret.counts = (HashMap<Integer, Integer>)(this.counts.clone());
			ret.unmodifiable = Collections.unmodifiableMap(ret.counts);
			return ret;
		}

		@Override
		protected Map<Integer, Integer> getValueInternal()
		{
			return this.unmodifiable;
		}

		@Override
		protected void reset()
		{
			this.counts.clear();
		}

		@Override
		protected void update(GameState state, Event event)
		{
			MagicSet objects = event.getResult(state);
			for(GameObject o: objects.getAll(GameObject.class))
			{
				int controllerID = o.getController(state).ID;
				if(this.counts.containsKey(controllerID))
					this.counts.put(controllerID, this.counts.get(controllerID) + 1);
				else
					this.counts.put(controllerID, 1);
			}
		}
	}

	public static MaximumPerPlayer instance(Class<? extends GameObjectsThisTurnCounter> tracker, SetGenerator players)
	{
		return new MaximumPerPlayer(tracker, players);
	}

	private SetGenerator players;
	private Class<? extends GameObjectsThisTurnCounter> tracker;

	private MaximumPerPlayer(Class<? extends GameObjectsThisTurnCounter> tracker, SetGenerator players)
	{
		this.players = players;
		this.tracker = tracker;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet players = this.players.evaluate(state, thisObject);
		Map<Integer, Integer> flag = state.getTracker(this.tracker).getValue(state);
		int count = 0;

		for(Player player: players.getAll(Player.class))
			if(flag.containsKey(player.ID))
			{
				int value = flag.get(player.ID);
				if(value > count)
					count = value;
			}
		return new MagicSet(count);
	}
}
