package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

/**
 * If at least one of the given players played a land this turn, evaluates to a
 * non empty set. If none of the given players played a land this turn,
 * evaluates to the empty set.
 */
public class PlayedALandThisTurn extends SetGenerator
{
	public static final class Tracker extends org.rnd.jmagic.engine.Tracker<Collection<Integer>>
	{
		private LinkedList<Integer> values = new LinkedList<Integer>();
		private Collection<Integer> unmodifiable = Collections.unmodifiableCollection(this.values);

		@SuppressWarnings("unchecked")
		@Override
		public Tracker clone()
		{
			Tracker ret = (Tracker)super.clone();
			ret.values = (LinkedList<Integer>)this.values.clone();
			ret.unmodifiable = Collections.unmodifiableCollection(ret.values);
			return ret;
		}

		@Override
		protected Collection<Integer> getValueInternal()
		{
			return this.unmodifiable;
		}

		@Override
		protected boolean match(GameState state, Event event)
		{
			return event.type == EventType.PLAY_LAND;
		}

		@Override
		protected void reset()
		{
			this.values.clear();
		}

		@Override
		protected void update(GameState state, Event event)
		{
			this.values.add(event.parameters.get(EventType.Parameter.PLAYER).evaluate(state, null).getOne(Player.class).ID);
		}
	}

	private SetGenerator who;

	private PlayedALandThisTurn(SetGenerator players)
	{
		this.who = players;
	}

	public static PlayedALandThisTurn instance(SetGenerator players)
	{
		return new PlayedALandThisTurn(players);
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		Turn currentTurn = state.currentTurn();
		if(currentTurn == null)
			return Empty.set;

		Collection<Integer> flagValue = state.getTracker(Tracker.class).getValue(state);
		for(Player p: this.who.evaluate(state, thisObject).getAll(Player.class))
			if(flagValue.contains(p.ID))
				return NonEmpty.set;
		return Empty.set;
	}
}
