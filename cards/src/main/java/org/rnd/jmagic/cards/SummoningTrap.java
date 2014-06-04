package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.Trap;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Name("Summoning Trap")
@Types({Type.INSTANT})
@SubTypes({SubType.TRAP})
@ManaCost("4GG")
@Printings({@Printings.Printed(ex = Expansion.ZENDIKAR, r = Rarity.RARE)})
@ColorIdentity({Color.GREEN})
public final class SummoningTrap extends Card
{
	/**
	 * keys are IDs of creature spells, values are IDs of who cast those spells
	 */
	public static final class CreatureSpellsCast extends Tracker<Map<Integer, Integer>>
	{
		private HashMap<Integer, Integer> value = new HashMap<Integer, Integer>();
		private Map<Integer, Integer> unmodifiable = Collections.unmodifiableMap(this.value);

		@Override
		@SuppressWarnings("unchecked")
		public CreatureSpellsCast clone()
		{
			CreatureSpellsCast ret = (CreatureSpellsCast)super.clone();
			ret.value = (HashMap<Integer, Integer>)this.value.clone();
			ret.unmodifiable = Collections.unmodifiableMap(ret.value);
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
			if(event.type != EventType.BECOMES_PLAYED)
				return false;

			GameObject played = event.parametersNow.get(EventType.Parameter.OBJECT).evaluate(state, null).getOne(GameObject.class);
			return played.getTypes().contains(Type.CREATURE);
		}

		@Override
		protected void reset()
		{
			this.value.clear();
		}

		@Override
		protected void update(GameState state, Event event)
		{
			Player caster = event.parametersNow.get(EventType.Parameter.PLAYER).evaluate(state, null).getOne(Player.class);
			GameObject played = event.parametersNow.get(EventType.Parameter.OBJECT).evaluate(state, null).getOne(GameObject.class);
			this.value.put(played.ID, caster.ID);
		}
	}

	public static final class HadACreatureSpellCounteredByAnOpponent extends SetGenerator
	{
		/**
		 * IDs of players who have had a creature spell they cast countered by a
		 * spell or ability an opponent controls
		 * 
		 * This flag requires the CreatureSpellsCast flag to function properly.
		 */
		public static final class CounterspellTracker extends Tracker<Collection<Integer>>
		{
			private HashSet<Integer> value = new HashSet<Integer>();
			private Collection<Integer> unmodifiable = Collections.unmodifiableSet(this.value);

			@Override
			@SuppressWarnings("unchecked")
			public CounterspellTracker clone()
			{
				CounterspellTracker ret = (CounterspellTracker)super.clone();
				ret.value = (HashSet<Integer>)this.value.clone();
				ret.unmodifiable = Collections.unmodifiableSet(ret.value);
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
				if(event.type != EventType.COUNTER)
					return false;

				GameObject countered = event.parametersNow.get(EventType.Parameter.OBJECT).evaluate(state, null).getOne(GameObject.class);
				CreatureSpellsCast flag = state.getTracker(CreatureSpellsCast.class);
				Map<Integer, Integer> flagValue = flag.getValue(state);
				if(!flagValue.containsKey(countered.ID))
					return false;

				GameObject countering = event.parametersNow.get(EventType.Parameter.CAUSE).evaluate(state, null).getOne(GameObject.class);
				if(countering == null)
					return false;
				Player counteringPlayer = countering.getController(state);

				int castingPlayerID = flagValue.get(countered.ID);
				Player castingPlayer = state.get(castingPlayerID);

				MagicSet opponentsOfCaster = OpponentsOf.get(state, castingPlayer);
				return opponentsOfCaster.contains(counteringPlayer);
			}

			@Override
			protected void reset()
			{
				this.value.clear();
			}

			@Override
			protected void update(GameState state, Event event)
			{
				GameObject countered = event.parametersNow.get(EventType.Parameter.OBJECT).evaluate(state, null).getOne(GameObject.class);
				CreatureSpellsCast flag = state.getTracker(CreatureSpellsCast.class);
				Map<Integer, Integer> flagValue = flag.getValue(state);

				int castingPlayerID = flagValue.get(countered.ID);
				this.value.add(castingPlayerID);
			}
		}

		private static SetGenerator _instance = new HadACreatureSpellCounteredByAnOpponent();

		public static SetGenerator instance()
		{
			return _instance;
		}

		private HadACreatureSpellCounteredByAnOpponent()
		{
			// singleton
		}

		@Override
		public MagicSet evaluate(GameState state, Identified thisObject)
		{
			MagicSet ret = new MagicSet();

			for(int ID: state.getTracker(CounterspellTracker.class).getValue(state))
				ret.add(state.get(ID));

			return ret;
		}
	}

	public SummoningTrap(GameState state)
	{
		super(state);

		// If a creature spell you cast this turn was countered by a spell or
		// ability an opponent controlled, you may pay (0) rather than pay
		// Summoning Trap's mana cost.
		state.ensureTracker(new CreatureSpellsCast());
		state.ensureTracker(new HadACreatureSpellCounteredByAnOpponent.CounterspellTracker());
		SetGenerator trapCondition = Intersect.instance(HadACreatureSpellCounteredByAnOpponent.instance(), You.instance());
		this.addAbility(new Trap(state, this.getName(), trapCondition, "If a creature spell you cast this turn was countered by a spell or ability an opponent controlled", "(0)"));

		// Look at the top seven cards of your library.
		SetGenerator yourLibrary = LibraryOf.instance(You.instance());
		SetGenerator topSeven = TopCards.instance(7, yourLibrary);
		EventFactory look = new EventFactory(EventType.LOOK, "Look at the top seven cards of your library.");
		look.parameters.put(EventType.Parameter.CAUSE, This.instance());
		look.parameters.put(EventType.Parameter.PLAYER, You.instance());
		look.parameters.put(EventType.Parameter.OBJECT, topSeven);
		this.addEffect(look);

		// You may put a creature card from among them onto the battlefield.
		SetGenerator creatureCards = Intersect.instance(HasType.instance(Type.CREATURE), topSeven);
		EventFactory putOntoBattlefield = new EventFactory(EventType.PUT_ONTO_BATTLEFIELD_CHOICE, "Put a creature card from among them onto the battlefield.");
		putOntoBattlefield.parameters.put(EventType.Parameter.CAUSE, This.instance());
		putOntoBattlefield.parameters.put(EventType.Parameter.CONTROLLER, You.instance());
		putOntoBattlefield.parameters.put(EventType.Parameter.OBJECT, creatureCards);
		this.addEffect(youMay(putOntoBattlefield, "You may put a creature card from among them onto the battlefield."));

		// Put the rest on the bottom of your library in any order.
		EventFactory putOnBottom = new EventFactory(EventType.PUT_INTO_LIBRARY, "Put the rest on the bottom of your library in any order.");
		putOnBottom.parameters.put(EventType.Parameter.CAUSE, This.instance());
		putOnBottom.parameters.put(EventType.Parameter.INDEX, numberGenerator(-1));
		putOnBottom.parameters.put(EventType.Parameter.OBJECT, topSeven);
		this.addEffect(putOnBottom);
	}
}
