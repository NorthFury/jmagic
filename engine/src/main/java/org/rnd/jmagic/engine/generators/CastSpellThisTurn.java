package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Resolves to the set of all players that cast a spell this turn.
 */
public class CastSpellThisTurn extends SetGenerator
{
	/** Values are IDs of players that cast a spell this turn. */
	public static final class Tracker extends org.rnd.jmagic.engine.Tracker<Set<Integer>>
	{
		private Set<Integer> values = new HashSet<Integer>();
		private Set<Integer> unmodifiable = Collections.unmodifiableSet(this.values);

		@Override
		protected Tracker clone()
		{
			Tracker ret = (Tracker)super.clone();
			ret.values = new HashSet<Integer>(this.values);
			ret.unmodifiable = Collections.unmodifiableSet(ret.values);
			return ret;
		}

		@Override
		protected Set<Integer> getValueInternal()
		{
			return this.unmodifiable;
		}

		@Override
		protected boolean match(GameState state, Event event)
		{
			return event.type == EventType.BECOMES_PLAYED;
		}

		@Override
		protected void reset()
		{
			this.values.clear();
		}

		@Override
		protected void update(GameState state, Event event)
		{
			// if there's no player, it certainly wasn't cast...
			// when a triggered ability triggers the player parameter is
			// nonexistant
			if(!event.parameters.containsKey(EventType.Parameter.PLAYER))
				return;

			Set<Player> players = event.parameters.get(EventType.Parameter.PLAYER).evaluate(state, null).getAll(Player.class);
			for(GameObject o: event.parameters.get(EventType.Parameter.OBJECT).evaluate(state, null).getAll(GameObject.class))
				if(o.isSpell())
					for(Player p: players)
						this.values.add(p.ID);
		}
	}

	private static final CastSpellThisTurn _instance = new CastSpellThisTurn();

	public static CastSpellThisTurn instance()
	{
		return _instance;
	}

	private CastSpellThisTurn()
	{
		// singleton
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		Tracker flag = state.getTracker(Tracker.class);
		MagicSet ret = new MagicSet();
		for(Integer i: flag.getValue(state))
			ret.add(state.get(i));
		return ret;
	}
}
