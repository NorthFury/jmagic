package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TokenSource extends SetGenerator
{
	/**
	 * This Tracker will note a Tokens source at its creation, and update its
	 * information whenever the token moves.
	 */
	public static class Tracker extends org.rnd.jmagic.engine.Tracker<Map<Integer, Integer>>
	{
		private Map<Integer, Integer> values = new HashMap<Integer, Integer>();
		private Map<Integer, Integer> unmodifiable = Collections.unmodifiableMap(this.values);

		@Override
		protected Tracker clone()
		{
			Tracker ret = (Tracker)super.clone();
			ret.values = new HashMap<Integer, Integer>(this.values);
			ret.unmodifiable = Collections.unmodifiableMap(ret.values);
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
			// This tracker never clears... sniff
		}

		@Override
		protected boolean match(GameState state, Event event)
		{
			return event.type == EventType.CREATE_TOKEN || event.type == EventType.MOVE_BATCH;
		}

		@Override
		protected void update(GameState state, Event event)
		{
			if(event.type == EventType.CREATE_TOKEN)
			{
				int tokenId = event.getResult(state).getOne(Token.class).ID;
				int source = event.getSource().ID;
				this.values.put(tokenId, source);
			}
			else
			{
				Set<Token> results = event.getResult(state).getAll(Token.class);
				for(GameObject o: results)
					if(o.pastSelf != -1 && this.values.containsKey(o.pastSelf))
						this.values.put(o.ID, this.values.get(o.pastSelf));
			}
		}
	}

	public static TokenSource instance(SetGenerator what)
	{
		return new TokenSource(what);
	}

	private SetGenerator what;

	private TokenSource(SetGenerator what)
	{
		this.what = what;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();
		Map<Integer, Integer> values = state.getTracker(Tracker.class).getValue(state);
		for(Token t: this.what.evaluate(state, thisObject).getAll(Token.class))
			if(values.containsKey(t.ID))
				ret.add(state.get(values.get(t.ID)));
		// else
		// throw new IllegalStateException("Token without a source");
		return ret;
	}

}
