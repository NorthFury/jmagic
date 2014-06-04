package org.rnd.jmagic.engine.generators;

import org.rnd.jmagic.engine.*;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public final class DrawnThisTurn extends SetGenerator
{
	public static final class DrawTracker extends Tracker<Collection<ZoneChange>>
	{
		private HashSet<ZoneChange> values = new HashSet<ZoneChange>();
		private Collection<ZoneChange> unmodifiable = Collections.unmodifiableSet(this.values);

		@Override
		public DrawnThisTurn.DrawTracker clone()
		{
			DrawnThisTurn.DrawTracker ret = (DrawnThisTurn.DrawTracker)super.clone();
			ret.values = new HashSet<ZoneChange>(this.values);
			ret.unmodifiable = Collections.unmodifiableSet(ret.values);
			return ret;
		}

		@Override
		protected Collection<ZoneChange> getValueInternal()
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
			ZoneChange draw = event.getResult().getOne(ZoneChange.class);
			this.values.add(draw);
		}
	}

	private static SetGenerator _instance;

	private DrawnThisTurn()
	{
		// singleton generator
	}

	public static SetGenerator instance()
	{
		if(_instance == null)
			_instance = new DrawnThisTurn();
		return _instance;
	}

	@Override
	public MagicSet evaluate(GameState state, Identified thisObject)
	{
		MagicSet ret = new MagicSet();
		for(ZoneChange z: state.getTracker(DrawnThisTurn.DrawTracker.class).getValue(state))
			ret.add(state.get(z.newObjectID));
		return ret;
	}
}