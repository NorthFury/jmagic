package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.lang.Integer;
import java.lang.String;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@Name("Sift Through Sands")
@Types({Type.INSTANT})
@SubTypes({SubType.ARCANE})
@ManaCost("1UU")
@Printings({@Printings.Printed(ex = Expansion.CHAMPIONS_OF_KAMIGAWA, r = Rarity.COMMON)})
@ColorIdentity({Color.BLUE})
public final class SiftThroughSands extends Card
{
	public static final class YouvePeeredAndReachedThisTurn extends SetGenerator
	{
		public static final class Tracker extends org.rnd.jmagic.engine.Tracker<Map<Integer, Collection<String>>>
		{
			HashMap<Integer, Collection<String>> value = new HashMap<Integer, Collection<String>>();
			Map<Integer, Collection<String>> unmodifiable = Collections.unmodifiableMap(this.value);

			@SuppressWarnings("unchecked")
			@Override
			protected org.rnd.jmagic.engine.Tracker<Map<Integer, Collection<String>>> clone()
			{
				Tracker ret = (Tracker)super.clone();
				ret.value = (HashMap<Integer, Collection<String>>)this.value.clone();
				ret.unmodifiable = Collections.unmodifiableMap(ret.value);
				return ret;
			}

			@Override
			protected Map<Integer, Collection<String>> getValueInternal()
			{
				return this.unmodifiable;
			}

			@Override
			protected boolean match(GameState state, Event event)
			{
				return (event.type == EventType.CAST_SPELL_OR_ACTIVATE_ABILITY);
			}

			@Override
			protected void reset()
			{
				this.value.clear();
			}

			@Override
			protected void update(GameState state, Event event)
			{
				Player player = event.parameters.get(EventType.Parameter.PLAYER).evaluate(state, null).getOne(Player.class);
				if(!this.value.containsKey(player.ID))
					this.value.put(player.ID, new LinkedList<String>());

				GameObject cast = event.parameters.get(EventType.Parameter.OBJECT).evaluate(state, null).getOne(GameObject.class);
				this.value.get(player.ID).add(cast.getName());
			}
		}

		private static SetGenerator _instance = null;

		public static SetGenerator instance()
		{
			if(_instance == null)
				_instance = new YouvePeeredAndReachedThisTurn();
			return _instance;
		}

		private YouvePeeredAndReachedThisTurn()
		{
			// singleton
		}

		@Override
		public MagicSet evaluate(GameState state, Identified thisObject)
		{
			Player you = ((GameObject)thisObject).getController(state);
			Collection<String> namesCast = state.getTracker(Tracker.class).getValue(state).get(you.ID);

			boolean peer = false;
			boolean reach = false;
			for(String name: namesCast)
			{
				if(name.equals("Peer Through Depths"))
					peer = true;
				else if(name.equals("Reach Through Mists"))
					reach = true;

				if(peer && reach)
					return NonEmpty.set;
			}
			return Empty.set;
		}
	}

	public SiftThroughSands(GameState state)
	{
		super(state);

		// Draw two cards, then discard a card.
		this.addEffect(drawCards(You.instance(), 2, "Draw two cards,"));
		this.addEffect(discardCards(You.instance(), 1, "then discard a card."));

		// If you've cast a spell named Peer Through Depths and a spell named
		// Reach Through Mists this turn, you may search your library for a card
		// named The Unspeakable, put it onto the battlefield, then shuffle your
		// library.
		EventFactory search = new EventFactory(EventType.SEARCH_LIBRARY_AND_PUT_INTO, "Search your library for a card named The Unspeakable, put it onto the battlefield, then shuffle your library.");
		search.parameters.put(EventType.Parameter.CAUSE, This.instance());
		search.parameters.put(EventType.Parameter.CONTROLLER, You.instance());
		search.parameters.put(EventType.Parameter.PLAYER, You.instance());
		search.parameters.put(EventType.Parameter.TO, Battlefield.instance());
		search.parameters.put(EventType.Parameter.TYPE, HasName.instance("The Unspeakable"));

		state.ensureTracker(new YouvePeeredAndReachedThisTurn.Tracker());
		EventFactory effect = new EventFactory(EventType.IF_CONDITION_THEN_ELSE, "\n\nIf you've cast a spell named Peer Through Depths and a spell named Reach Through Mists this turn, you may search your library for a card named The Unspeakable, put it onto the battlefield, then shuffle your library.");
		effect.parameters.put(EventType.Parameter.IF, YouvePeeredAndReachedThisTurn.instance());
		effect.parameters.put(EventType.Parameter.THEN, Identity.instance(youMay(search, "You may search your library for a card named The Unspeakable, put it onto the battlefield, then shuffle your library.")));
		this.addEffect(effect);
	}
}
