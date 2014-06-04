package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class LandsPlayedThisTurn extends SetGenerator
{
	public static final class LandsPlayedTracker extends Tracker<Map<Integer, Integer>>
	{
		private HashMap<Integer, Integer> counts = new HashMap<Integer, Integer>();
		private Map<Integer, Integer> unmodifiable = Collections.unmodifiableMap(this.counts);

		@SuppressWarnings("unchecked")
		@Override
		public LandsPlayedTracker clone()
		{
			LandsPlayedTracker ret = (LandsPlayedTracker)super.clone();
			ret.counts = (HashMap<Integer, Integer>)this.counts.clone();
			ret.unmodifiable = Collections.unmodifiableMap(ret.counts);
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
			return (event.type == EventType.PLAY_LAND);
		}

		@Override
		protected void reset()
		{
			this.counts.clear();
		}

		@Override
		protected void update(GameState state, Event event)
		{
			Player player = event.parametersNow.get(EventType.Parameter.PLAYER).evaluate(state, null).getOne(Player.class);
			int key = player.ID;

			if(this.counts.containsKey(key))
				this.counts.put(key, this.counts.get(key) + 1);
			else
				this.counts.put(key, 1);
		}
	}

	public static SetGenerator instance(SetGenerator who)
	{
		return new LandsPlayedThisTurn(who);
	}

	private SetGenerator who;

	private LandsPlayedThisTurn(SetGenerator who)
	{
		this.who = who;
	}

	public static int get(Player player)
	{
		Map<Integer, Integer> playedLandCounts = player.state.getTracker(LandsPlayedTracker.class).getValue(player.state);
		if(playedLandCounts.containsKey(player.ID))
			return playedLandCounts.get(player.ID);
		return 0;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();
		Set<Player> players = this.who.evaluate(state, thisObject).getAll(Player.class);
		Map<Integer, Integer> playedLandCounts = state.getTracker(LandsPlayedTracker.class).getValue(state);
		for(Player player: players)
		{
			if(playedLandCounts.containsKey(player.ID))
				ret.add(playedLandCounts.get(player.ID));
			else
				ret.add(0);
		}
		return ret;
	}
}
