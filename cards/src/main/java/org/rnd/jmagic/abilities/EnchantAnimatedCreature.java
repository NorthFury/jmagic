package org.rnd.jmagic.abilities;

import org.rnd.jmagic.abilities.keywords.Enchant;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class EnchantAnimatedCreature extends Enchant
{
	public static class AnimatedBy extends SetGenerator
	{
		public static AnimatedBy instance(SetGenerator card, Class<? extends ReanimationTracker> trackerClass)
		{
			return new AnimatedBy(card, trackerClass);
		}

		private SetGenerator what;
		private Class<? extends ReanimationTracker> trackerClass;

		private AnimatedBy(SetGenerator what, Class<? extends ReanimationTracker> trackerClass)
		{
			this.what = what;
			this.trackerClass = trackerClass;
		}

		@Override
		public MagicSet evaluate(GameState state, Identified thisObject)
		{
			MagicSet ret = new MagicSet();

			Map<Integer, Set<Integer>> animated = state.getTracker(this.trackerClass).getValue(state);

			for(Identified i: this.what.evaluate(state, thisObject).getAll(Identified.class))
				if(animated.containsKey(i.ID))
					for(Integer id: animated.get(i.ID))
						ret.add(state.get(id));

			return ret;
		}

	}

	public static abstract class ReanimationTracker extends Tracker<Map<Integer, Set<Integer>>>
	{
		// keys are Animate Dead triggers, values are collections of objects
		// moved with them
		protected HashMap<Integer, Set<Integer>> movedWithAD = new HashMap<Integer, Set<Integer>>();
		private Map<Integer, Set<Integer>> unmodifiable = Collections.unmodifiableMap(this.movedWithAD);

		@Override
		protected Map<Integer, Set<Integer>> getValueInternal()
		{
			return this.unmodifiable;
		}

		@Override
		protected void reset()
		{
			// This tracker deliberately does not reset.
		}

		protected abstract Class<? extends TriggeredAbility> triggerClass();

		@Override
		protected void update(GameState state, Event event)
		{
			for(ZoneChange change: event.parametersNow.get(EventType.Parameter.TARGET).evaluate(state, null).getAll(ZoneChange.class))
			{
				if(change.causeID != 0)
				{
					Identified cause = state.get(change.causeID);
					if(this.triggerClass().isInstance(cause))
					{
						int source = ((TriggeredAbility)cause).getSourceID();
						if(!this.movedWithAD.containsKey(source))
							this.movedWithAD.put(source, new HashSet<Integer>());
						this.movedWithAD.get(source).add(change.newObjectID);
					}
				}
			}
		}

		@Override
		protected boolean match(GameState state, Event event)
		{
			if(event.type != EventType.MOVE_BATCH)
				return false;

			for(ZoneChange change: event.parametersNow.get(EventType.Parameter.TARGET).evaluate(state, null).getAll(ZoneChange.class))
				if(change.causeID != 0 && this.triggerClass().isInstance(state.<Identified>get(change.causeID)))
					return true;
			return false;
		}
	}

	public static final class DefaultReanimationTracker extends ReanimationTracker
	{
		@Override
		protected Class<? extends TriggeredAbility> triggerClass()
		{
			return AnimateDeadCreature.class;
		}
	}

	public EnchantAnimatedCreature(GameState state, String cardName)
	{
		this(state, cardName, new DefaultReanimationTracker());
	}

	/** no need to ensure the tracker you give to this function, i got it dawg */
	public EnchantAnimatedCreature(GameState state, String cardName, ReanimationTracker tracker)
	{
		super(state, "creature put onto the battlefield with " + cardName, AnimatedBy.instance(This.instance(), tracker.getClass()));
		state.ensureTracker(tracker);
	}
}
