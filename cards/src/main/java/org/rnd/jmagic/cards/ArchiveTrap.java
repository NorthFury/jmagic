package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.Trap;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Name("Archive Trap")
@Types({Type.INSTANT})
@SubTypes({SubType.TRAP})
@ManaCost("3UU")
@Printings({@Printings.Printed(ex = Expansion.ZENDIKAR, r = Rarity.RARE)})
@ColorIdentity({Color.BLUE})
public final class ArchiveTrap extends Card
{
	public static final class SearchedTheirLibraryThisTurn extends SetGenerator
	{
		private static class SearchTracker extends Tracker<Set<Integer>>
		{
			private HashSet<Integer> who = new HashSet<Integer>();
			private Set<Integer> unmodifiable = Collections.unmodifiableSet(this.who);

			@SuppressWarnings("unchecked")
			@Override
			public SearchTracker clone()
			{
				SearchTracker ret = (SearchTracker)super.clone();
				ret.who = (HashSet<Integer>)this.who.clone();
				ret.unmodifiable = Collections.unmodifiableSet(ret.who);
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
				if(event.type != EventType.SEARCH)
					return false;

				MagicSet searched = event.parametersNow.get(EventType.Parameter.CARD).evaluate(state, null);
				Player who = event.parametersNow.get(EventType.Parameter.PLAYER).evaluate(state, null).getOne(Player.class);

				for(Zone zone: searched.getAll(Zone.class))
				{
					if(!zone.isLibrary())
						continue;

					if(zone.getOwner(state).equals(who))
						return true;
				}

				return false;
			}

			@Override
			protected void reset()
			{
				this.who.clear();
			}

			@Override
			protected void update(GameState state, Event event)
			{
				Player who = event.parametersNow.get(EventType.Parameter.PLAYER).evaluate(state, null).getOne(Player.class);
				this.who.add(who.ID);
			}
		}

		private static SetGenerator _instance = new SearchedTheirLibraryThisTurn();

		private SearchedTheirLibraryThisTurn()
		{
			// singleton generator
		}

		public static SetGenerator instance()
		{
			return _instance;
		}

		@Override
		public MagicSet evaluate(GameState state, Identified thisObject)
		{
			MagicSet ret = new MagicSet();
			Set<Integer> playerIDs = state.getTracker(SearchTracker.class).getValue(state);
			for(int playerID: playerIDs)
				ret.add(state.get(playerID));
			return ret;
		}
	}

	public ArchiveTrap(GameState state)
	{
		super(state);

		// If an opponent searched his or her library this turn, you may pay (0)
		// rather than pay Archive Trap's mana cost.
		state.ensureTracker(new SearchedTheirLibraryThisTurn.SearchTracker());
		SetGenerator trapCondition = Intersect.instance(SearchedTheirLibraryThisTurn.instance(), OpponentsOf.instance(You.instance()));
		this.addAbility(new Trap(state, this.getName(), trapCondition, "If an opponent searched his or her library this turn", "(0)"));

		// Target opponent puts the top thirteen cards of his or her library
		// into his or her graveyard.
		Target target = this.addTarget(OpponentsOf.instance(You.instance()), "target opponent");
		this.addEffect(millCards(targetedBy(target), 13, "Target opponent puts the top thirteen cards of his or her library into his or her graveyard."));
	}
}
