package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;
import org.rnd.util.NumberNames;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Name("Agonizing Memories")
@Types({Type.SORCERY})
@ManaCost("2BB")
@Printings({@Printings.Printed(ex = Expansion.TENTH_EDITION, r = Rarity.UNCOMMON), @Printings.Printed(ex = Expansion.SEVENTH_EDITION, r = Rarity.UNCOMMON), @Printings.Printed(ex = Expansion.SIXTH_EDITION, r = Rarity.UNCOMMON), @Printings.Printed(ex = Expansion.WEATHERLIGHT, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.BLACK})
public final class AgonizingMemories extends Card
{
	public static final PlayerInterface.ChooseReason REASON = new PlayerInterface.ChooseReason("AgonizingMemories", "Order the cards on top of the opponents library", false);

	/**
	 * @eparam CAUSE: the cause for the search and move
	 * @eparam PLAYER: the player whose hand will be looked at
	 * @eparam CONTROLLER: the player who will be looking
	 * @eparam NUMBER: the number of cards to choose
	 * @eparam RESULT: empty
	 */
	public static final EventType AGONIZING_MEMORIES_EVENT = new EventType("AGONIZING_MEMORIES_EVENT")
	{
		@Override
		public Parameter affects()
		{
			return null;
		}

		@Override
		public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
		{
			Player victim = parameters.get(EventType.Parameter.PLAYER).getOne(Player.class);
			Player chooser = parameters.get(EventType.Parameter.CONTROLLER).getOne(Player.class);
			int number = Sum.get(parameters.get(EventType.Parameter.NUMBER));

			boolean ret = true;

			Map<EventType.Parameter, MagicSet> searchParameters = new HashMap<EventType.Parameter, MagicSet>();
			searchParameters.put(EventType.Parameter.CAUSE, parameters.get(EventType.Parameter.CAUSE));
			searchParameters.put(EventType.Parameter.PLAYER, new MagicSet(chooser));
			searchParameters.put(EventType.Parameter.NUMBER, new MagicSet(number));
			searchParameters.put(EventType.Parameter.CARD, new MagicSet(victim.getHand(game.actualState)));
			Event searchEvent = createEvent(game, chooser + " looks at " + victim + "'s hand and chooses " + NumberNames.get(number) + " cards.", EventType.SEARCH, searchParameters);
			ret = searchEvent.perform(event, true) && ret;

			// Perform refreshes the state, so update our stateful
			// variables.
			victim = victim.getActual();
			chooser = chooser.getActual();

			MagicSet cards = searchEvent.getResult();

			List<GameObject> chosen = chooser.sanitizeAndChoose(game.actualState, cards.size(), cards.getAll(GameObject.class), PlayerInterface.ChoiceType.MOVEMENT_LIBRARY, REASON);

			for(GameObject object: chosen)
			{
				Map<EventType.Parameter, MagicSet> moveParameters = new HashMap<EventType.Parameter, MagicSet>();
				moveParameters.put(EventType.Parameter.CAUSE, parameters.get(EventType.Parameter.CAUSE));
				moveParameters.put(EventType.Parameter.TO, new MagicSet(victim.getLibrary(game.actualState)));
				moveParameters.put(EventType.Parameter.OBJECT, new MagicSet(object));
				Event moveEvent = createEvent(game, chooser + " puts " + object + " on top of " + victim + "'s library.", EventType.MOVE_OBJECTS, moveParameters);
				ret = moveEvent.perform(event, true) && ret;

				// Perform refreshes the state, so update our stateful
				// variables.
				victim = victim.getActual();
				chooser = chooser.getActual();
			}

			event.setResult(Empty.set);

			return ret;
		}
	};

	public AgonizingMemories(GameState state)
	{
		super(state);

		Target target = this.addTarget(Players.instance(), "target player");

		EventType.ParameterMap parameters = new EventType.ParameterMap();
		parameters.put(EventType.Parameter.CAUSE, This.instance());
		parameters.put(EventType.Parameter.PLAYER, targetedBy(target));
		parameters.put(EventType.Parameter.CONTROLLER, ControllerOf.instance(This.instance()));
		parameters.put(EventType.Parameter.NUMBER, numberGenerator(2));
		this.addEffect(new EventFactory(AGONIZING_MEMORIES_EVENT, parameters, "Look at target player's hand and choose two cards from it. Put them on top of that player's library in any order."));
	}
}
