package org.rnd.jmagic.abilities.keywords;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 702.37. Storm
 * 
 * 702.37a Storm is a triggered ability that functions on the stack. "Storm"
 * means "When you cast this spell, put a copy of it onto the stack for each
 * other spell that was cast before it this turn. If the spell has any targets,
 * you may choose new targets for any of the copies."
 */
public final class Storm extends Keyword
{
	public static class StormTracker extends Tracker<List<Integer>>
	{
		private List<Integer> spellIDs = new LinkedList<Integer>();
		private List<Integer> unmodifiable = Collections.unmodifiableList(this.spellIDs);

		@Override
		public StormTracker clone()
		{
			StormTracker ret = (StormTracker)super.clone();
			ret.spellIDs = new LinkedList<Integer>(this.spellIDs);
			ret.unmodifiable = Collections.unmodifiableList(ret.spellIDs);
			return ret;
		}

		@Override
		protected List<Integer> getValueInternal()
		{
			return this.unmodifiable;
		}

		@Override
		protected boolean match(GameState state, Event event)
		{
			if(event.type == EventType.CAST_SPELL_OR_ACTIVATE_ABILITY)
				if(event.getResult(state).getOne(GameObject.class).isSpell())
					return true;
			return false;
		}

		@Override
		protected void reset()
		{
			this.spellIDs.clear();
		}

		@Override
		protected void update(GameState state, Event event)
		{
			GameObject cast = event.getResult(state).getOne(GameObject.class);
			this.spellIDs.add(cast.ID);
		}

	}

	public Storm(GameState state)
	{
		super(state, "Storm");
	}

	@Override
	public List<NonStaticAbility> createNonStaticAbilities()
	{
		return Collections.<NonStaticAbility>singletonList(new StormAbility(this.state));
	}

	public static class StormCount extends SetGenerator
	{
		private SetGenerator what;

		public static StormCount instance(SetGenerator whichSpell)
		{
			return new StormCount(whichSpell);
		}

		private StormCount(SetGenerator whichSpell)
		{
			this.what = whichSpell;
		}

		@Override
		public MagicSet evaluate(GameState state, Identified thisObject)
		{
			GameObject whichSpell = this.what.evaluate(state, thisObject).getOne(GameObject.class);

			List<Integer> flagValue = state.getTracker(StormTracker.class).getValue(state);
			int count = flagValue.indexOf(whichSpell.ID);
			return count == -1 ? ZERO : new MagicSet(count);
		}
	}

	public static final class StormAbility extends EventTriggeredAbility
	{
		public StormAbility(GameState state)
		{
			super(state, "When you cast this spell, put a copy of it onto the stack for each other spell that was cast before it this turn. If the spell has any targets, you may choose new targets for any of the copies.");
			this.triggersFromStack();

			this.addPattern(whenYouCastThisSpell());

			EventFactory factory = new EventFactory(EventType.COPY_SPELL_OR_ABILITY, "Put a copy of it onto the stack for each other spell that was cast before it this turn. If the spell has any targets, you may choose new targets for any of the copies.");
			factory.parameters.put(EventType.Parameter.CAUSE, This.instance());
			factory.parameters.put(EventType.Parameter.OBJECT, ABILITY_SOURCE_OF_THIS);
			factory.parameters.put(EventType.Parameter.PLAYER, You.instance());
			factory.parameters.put(EventType.Parameter.NUMBER, StormCount.instance(ABILITY_SOURCE_OF_THIS));
			this.addEffect(factory);

			state.ensureTracker(new StormTracker());
		}
	}
}
