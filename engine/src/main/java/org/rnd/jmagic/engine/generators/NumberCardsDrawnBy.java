package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class NumberCardsDrawnBy extends SetGenerator
{
	public static final class DrawCounter extends Tracker<Map<Integer, Integer>>
	{
		private HashMap<Integer, Integer> values = new HashMap<Integer, Integer>();
		private Map<Integer, Integer> unmodifiable = Collections.unmodifiableMap(this.values);

		@SuppressWarnings("unchecked")
		@Override
		public NumberCardsDrawnBy.DrawCounter clone()
		{
			NumberCardsDrawnBy.DrawCounter ret = (NumberCardsDrawnBy.DrawCounter)super.clone();
			ret.values = (HashMap<Integer, Integer>)this.values.clone();
			ret.unmodifiable = Collections.unmodifiableMap(ret.values);
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
			return (event.type == EventType.DRAW_ONE_CARD);
		}

		@Override
		protected void reset()
		{
			this.values.clear();
		}

		@Override
		protected void update(GameState state, Event event)
		{
			int playerID = event.parametersNow.get(EventType.Parameter.PLAYER).evaluate(state, null).getOne(Player.class).ID;

			if(!this.values.containsKey(playerID))
				this.values.put(playerID, 1);
			else
				this.values.put(playerID, this.values.get(playerID) + 1);
		}
	}

	public static NumberCardsDrawnBy instance(SetGenerator what)
	{
		return new NumberCardsDrawnBy(what);
	}

	private SetGenerator what;

	private NumberCardsDrawnBy(SetGenerator what)
	{
		this.what = what;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();
		Map<Integer, Integer> flagValues = state.getTracker(NumberCardsDrawnBy.DrawCounter.class).getValue(state);
		for(Player player: this.what.evaluate(state, thisObject).getAll(Player.class))
		{
			Integer numDrawn = flagValues.get(player.ID);
			if(numDrawn == null)
				numDrawn = 0;
			ret.add(numDrawn);
		}
		return ret;
	}
}