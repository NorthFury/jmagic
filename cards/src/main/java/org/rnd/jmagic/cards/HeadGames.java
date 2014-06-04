package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.Map;

@Name("Head Games")
@Types({Type.SORCERY})
@ManaCost("3BB")
@Printings({@Printings.Printed(ex = Expansion.TENTH_EDITION, r = Rarity.RARE), @Printings.Printed(ex = Expansion.ONSLAUGHT, r = Rarity.RARE)})
@ColorIdentity({Color.BLACK})
public final class HeadGames extends Card
{
	/**
	 * @eparam CAUSE: the head games
	 * @eparam PLAYER: the player doing the searching
	 * @eparam TARGET: the target player
	 * @eparam RESULT: empty
	 */
	public static final EventType HEAD_GAMES_EVENT = new EventType("HEAD_GAMES_EVENT")
	{
		@Override
		public Parameter affects()
		{
			return null;
		}

		@Override
		public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
		{
			MagicSet cause = parameters.get(EventType.Parameter.CAUSE);
			Player target = parameters.get(EventType.Parameter.TARGET).getOne(Player.class);
			Zone targetsHand = target.getHand(game.actualState);
			MagicSet targetsHandSet = new MagicSet(targetsHand);
			MagicSet targetsLibrary = new MagicSet(target.getLibrary(game.actualState));

			Map<EventType.Parameter, MagicSet> moveParams = new HashMap<EventType.Parameter, MagicSet>();
			moveParams.put(EventType.Parameter.CAUSE, cause);
			moveParams.put(EventType.Parameter.TO, targetsLibrary);
			moveParams.put(EventType.Parameter.INDEX, new MagicSet(1));
			moveParams.put(EventType.Parameter.OBJECT, new MagicSet(targetsHand.objects));
			Event libraryEvent = createEvent(game, "Target opponent puts the cards from his or her hand on top of his or her library.", EventType.MOVE_OBJECTS, moveParams);
			boolean ret = libraryEvent.perform(event, true);

			Map<EventType.Parameter, MagicSet> searchParams = new HashMap<EventType.Parameter, MagicSet>();
			searchParams.put(EventType.Parameter.CAUSE, cause);
			searchParams.put(EventType.Parameter.PLAYER, parameters.get(EventType.Parameter.PLAYER));
			searchParams.put(EventType.Parameter.NUMBER, new MagicSet(libraryEvent.getResult().size()));
			searchParams.put(EventType.Parameter.CARD, targetsLibrary);
			Event searchEvent = createEvent(game, "Search that player's library for that many cards.", EventType.SEARCH, searchParams);
			ret = searchEvent.perform(event, true) && ret;

			Map<EventType.Parameter, MagicSet> handParams = new HashMap<EventType.Parameter, MagicSet>();
			handParams.put(EventType.Parameter.CAUSE, cause);
			handParams.put(EventType.Parameter.TO, targetsHandSet);
			handParams.put(EventType.Parameter.OBJECT, searchEvent.getResult());
			Event handEvent = createEvent(game, "The player puts those cards into his or her hand.", EventType.MOVE_OBJECTS, handParams);
			ret = handEvent.perform(event, true) && ret;

			Map<EventType.Parameter, MagicSet> shuffleParams = new HashMap<EventType.Parameter, MagicSet>();
			shuffleParams.put(EventType.Parameter.CAUSE, cause);
			shuffleParams.put(EventType.Parameter.PLAYER, new MagicSet(target));
			Event shuffleEvent = createEvent(game, "Then shuffles his or her library.", EventType.SHUFFLE_LIBRARY, shuffleParams);
			ret = shuffleEvent.perform(event, true) && ret;

			event.setResult(Empty.set);
			return ret;
		}
	};

	public HeadGames(GameState state)
	{
		super(state);

		Target target = this.addTarget(OpponentsOf.instance(You.instance()), "target opponent");

		EventType.ParameterMap parameters = new EventType.ParameterMap();
		parameters.put(EventType.Parameter.CAUSE, This.instance());
		parameters.put(EventType.Parameter.TARGET, targetedBy(target));
		parameters.put(EventType.Parameter.PLAYER, You.instance());
		this.addEffect(new EventFactory(HEAD_GAMES_EVENT, parameters, "Target opponent puts the cards from his or her hand on top of his or her library. Search that player's library for that many cards. The player puts those cards into his or her hand, then shuffles his or her library."));
	}
}
