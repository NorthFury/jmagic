package org.rnd.jmagic.abilities.keywords;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;
import org.rnd.util.NumberNames;
import org.rnd.util.NumberRange;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 702.79. Devour
 * 
 * 702.79a Devour is a static ability. "Devour N" means "As this object enters
 * the battlefield, you may sacrifice any number of creatures. This permanent
 * enters the battlefield with N +1/+1 counters on it for each creature
 * sacrificed this way."
 * 
 * 702.79b Some objects have abilities that refer to the number of creatures the
 * permanent devoured. "It devoured" means "sacrificed as a result of its devour
 * ability as it entered the battlefield."
 */
public final class Devour extends Keyword
{
	private final int N;

	public Devour(GameState state, int N)
	{
		super(state, "Devour " + N);
		this.N = N;
	}

	@Override
	public Devour create(Game game)
	{
		return new Devour(game.physicalState, this.N);
	}

	@Override
	public List<StaticAbility> createStaticAbilities()
	{
		return Collections.<StaticAbility>singletonList(new DevourAbility(this.state, this.N));
	}

	public static final class DevourAbility extends StaticAbility
	{
		private static String reminderText(int N)
		{
			String ret = "As this enters the battlefield, you may sacrifice any number of creatures. This creature enters the battlefield with ";

			if(N == 2)
				ret += "twice ";
			else if(N != 1)
				ret += NumberNames.get(N) + " times ";

			ret += "that many +1/+1 counters on it.";
			return ret;
		}

		private final int N;

		public DevourAbility(GameState state, int N)
		{
			super(state, reminderText(N));

			this.N = N;

			ZoneChangeReplacementEffect replacement = new ZoneChangeReplacementEffect(this.game, "As this object enters the battlefield, you may sacrifice any number of creatures. This permanent enters the battlefield with " + N + " +1/+1 counters on it for each creature sacrificed this way.");
			replacement.addPattern(asThisEntersTheBattlefield());
			this.canApply = NonEmpty.instance();

			EventFactory effect = new EventFactory(DEVOUR_EVENT, "You may sacrifice any number of creatures. This permanent enters the battlefield with " + N + " +1/+1 counters on it for each creature sacrificed this way.");
			effect.parameters.put(EventType.Parameter.OBJECT, NewObjectOf.instance(replacement.replacedByThis()));
			effect.parameters.put(EventType.Parameter.PLAYER, You.instance());
			effect.parameters.put(EventType.Parameter.CHOICE, CREATURES_YOU_CONTROL);
			effect.parameters.put(EventType.Parameter.NUMBER, numberGenerator(N));
			replacement.addEffect(effect);

			this.addEffectPart(replacementEffectPart(replacement));
		}

		@Override
		public DevourAbility create(Game game)
		{
			return new DevourAbility(game.physicalState, this.N);
		}

		/**
		 * @eparam OBJECT: the creature that will receive counters
		 * @eparam PLAYER: controller of CAUSE
		 * @eparam CHOICE: creatures PLAYER controls
		 * @eparam NUMBER: the number of counters per creature sacrificed
		 */
		public static final EventType DEVOUR_EVENT = new EventType("DEVOUR_EVENT")
		{
			@Override
			public Parameter affects()
			{
				return null;
			}

			@Override
			public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
			{
				event.setResult(Empty.set);

				MagicSet thisCreature = parameters.get(Parameter.OBJECT);
				MagicSet player = parameters.get(Parameter.PLAYER);
				MagicSet choices = parameters.get(Parameter.CHOICE);

				Map<Parameter, MagicSet> sacrificeParameters = new HashMap<Parameter, MagicSet>();
				sacrificeParameters.put(Parameter.CAUSE, thisCreature);
				sacrificeParameters.put(Parameter.NUMBER, new MagicSet(new NumberRange(0, null)));
				sacrificeParameters.put(Parameter.CHOICE, choices);
				sacrificeParameters.put(Parameter.PLAYER, player);
				Event sacrifice = createEvent(game, "You may sacrifice any number of creatures", EventType.SACRIFICE_CHOICE, sacrificeParameters);
				sacrifice.perform(event, true);
				MagicSet sacrificed = sacrifice.getResult();

				int N = parameters.get(Parameter.NUMBER).getOne(Integer.class);

				Map<Parameter, MagicSet> counterParameters = new HashMap<Parameter, MagicSet>();
				counterParameters.put(Parameter.CAUSE, thisCreature);
				counterParameters.put(Parameter.COUNTER, new MagicSet(Counter.CounterType.PLUS_ONE_PLUS_ONE));
				counterParameters.put(Parameter.NUMBER, new MagicSet(N * sacrificed.size()));
				counterParameters.put(Parameter.OBJECT, thisCreature);
				Event counters = createEvent(game, "This permanent enters the battlefield with " + N + " +1/+1 counters on it for each creature sacrificed this way.", EventType.PUT_COUNTERS, counterParameters);
				counters.perform(event, true);

				return true;
			}
		};
	}
}
