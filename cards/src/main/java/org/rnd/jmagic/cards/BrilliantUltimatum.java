package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Name("Brilliant Ultimatum")
@Types({Type.SORCERY})
@ManaCost("WWUUUBB")
@Printings({@Printings.Printed(ex = Expansion.SHARDS_OF_ALARA, r = Rarity.RARE)})
@ColorIdentity({Color.BLUE, Color.WHITE, Color.BLACK})
public final class BrilliantUltimatum extends Card
{
	public static final PlayerInterface.ChooseReason REASON = new PlayerInterface.ChooseReason("BrilliantUltimatum", "Choose a pile to play cards from.", true);

	/**
	 * @eparam CAUSE: brilliant ultimatum
	 * @eparam PLAYER: controller of CAUSE
	 * @eparam CARD: top 5 cards of PLAYER's library
	 */
	public static final EventType BRILLIANT_ULTIMATUM_EVENT = new EventType("BRILLIANT_ULTIMATUM_EVENT")
	{
		@Override
		public Parameter affects()
		{
			return null;
		}

		@Override
		public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
		{
			Event exile = exile(Identity.instance(parameters.get(Parameter.CARD)), "Exile the top five cards of your library").createEvent(game, event.getSource());
			exile.perform(event, true);

			Player you = parameters.get(Parameter.PLAYER).getOne(Player.class).getActual();
			Set<Player> opponents = OpponentsOf.get(game.actualState, you).getAll(Player.class);
			Player anOpponent = you.sanitizeAndChoose(game.actualState, 1, opponents, PlayerInterface.ChoiceType.PLAYER, PlayerInterface.ChooseReason.AN_OPPONENT).iterator().next();

			MagicSet thoseCards = NewObjectOf.instance(exile.getResultGenerator()).evaluate(game, null);
			Collection<Pile> piles = anOpponent.separateIntoPiles(2, thoseCards);

			Pile chosenPile = you.sanitizeAndChoose(game.actualState, 1, piles, PlayerInterface.ChoiceType.PILE, REASON).iterator().next();

			Map<Parameter, MagicSet> playParameters = new HashMap<Parameter, MagicSet>();
			playParameters.put(Parameter.CAUSE, parameters.get(Parameter.CAUSE));
			playParameters.put(Parameter.PLAYER, new MagicSet(you));
			playParameters.put(Parameter.OBJECT, new MagicSet(chosenPile));
			Event playStuff = createEvent(game, "You may play any number of cards from one of those piles without paying their mana costs.", PLAY_WITHOUT_PAYING_MANA_COSTS, playParameters);
			playStuff.perform(event, true);

			event.setResult(Empty.set);
			return true;
		}
	};

	public BrilliantUltimatum(GameState state)
	{
		super(state);

		// Exile the top five cards of your library. An opponent separates those
		// cards into two piles. You may play any number of cards from one of
		// those piles without paying their mana costs.
		EventFactory effect = new EventFactory(BRILLIANT_ULTIMATUM_EVENT, "Exile the top five cards of your library. An opponent separates those cards into two piles. You may play any number of cards from one of those piles without paying their mana costs.");
		effect.parameters.put(EventType.Parameter.CAUSE, This.instance());
		effect.parameters.put(EventType.Parameter.PLAYER, You.instance());
		effect.parameters.put(EventType.Parameter.CARD, TopCards.instance(5, LibraryOf.instance(You.instance())));
		this.addEffect(effect);
	}
}
