package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Flash;
import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@Name("Vendilion Clique")
@SuperTypes({SuperType.LEGENDARY})
@Types({Type.CREATURE})
@SubTypes({SubType.FAERIE, SubType.WIZARD})
@ManaCost("1UU")
@Printings({@Printings.Printed(ex = Expansion.MORNINGTIDE, r = Rarity.RARE)})
@ColorIdentity({Color.BLUE})
public final class VendilionClique extends Card
{
	public static final PlayerInterface.ChooseReason REASON = new PlayerInterface.ChooseReason("VendilionClique", "Put a card on the bottom of its owner's library.", true);

	/**
	 * @eparam CAUSE: Clique's ability
	 * @eparam PLAYER: you
	 * @eparam TARGET: the target of Clique's ability
	 * @eparam RESULT: empty
	 */
	public static EventType CARD_NAPPING = new EventType("CARD_NAPPING")
	{
		@Override
		public boolean attempt(Game game, Event event, Map<Parameter, MagicSet> parameters)
		{
			Player target = parameters.get(Parameter.TARGET).getOne(Player.class);
			for(GameObject card: target.getHand(game.actualState).objects)
				if(!card.getTypes().contains(Type.LAND))
					return true;
			return false;
		}

		@Override
		public Parameter affects()
		{
			return Parameter.TARGET;
		}

		@Override
		public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
		{
			MagicSet cause = parameters.get(Parameter.CAUSE);
			Player you = parameters.get(Parameter.PLAYER).getOne(Player.class);
			Player target = parameters.get(Parameter.TARGET).getOne(Player.class);

			// choose a nonland card from it.
			Collection<GameObject> nonlandCards = new LinkedList<GameObject>();
			for(GameObject card: target.getHand(game.actualState).objects)
				if(!card.getTypes().contains(Type.LAND))
					nonlandCards.add(card);
			MagicSet chosen = new MagicSet(you.sanitizeAndChoose(game.actualState, 1, nonlandCards, PlayerInterface.ChoiceType.OBJECTS, REASON));

			// If you do, that player reveals the chosen card,
			Map<Parameter, MagicSet> revealParameters = new HashMap<Parameter, MagicSet>();
			revealParameters.put(Parameter.CAUSE, cause);
			revealParameters.put(Parameter.OBJECT, chosen);
			createEvent(game, "That player reveals that card", REVEAL, revealParameters).perform(event, true);

			// puts it on the bottom of his or her library,
			Map<Parameter, MagicSet> moveParameters = new HashMap<Parameter, MagicSet>();
			moveParameters.put(Parameter.CAUSE, cause);
			moveParameters.put(Parameter.INDEX, NEGATIVE_ONE);
			moveParameters.put(Parameter.OBJECT, chosen);
			createEvent(game, "That player puts that card on the bottom of his or her library", PUT_INTO_LIBRARY, moveParameters).perform(event, true);

			// then draws a card.
			Map<Parameter, MagicSet> drawParameters = new HashMap<Parameter, MagicSet>();
			drawParameters.put(Parameter.CAUSE, cause);
			drawParameters.put(Parameter.NUMBER, ONE);
			drawParameters.put(Parameter.PLAYER, new MagicSet(target));
			createEvent(game, "That player draws a card", DRAW_CARDS, drawParameters).perform(event, true);

			event.setResult(Empty.set);
			return true;
		}

	};

	public static EventType CLIQUE_EFFECT = new EventType("CLIQUE_EFFECT")
	{
		@Override
		public Parameter affects()
		{
			return Parameter.TARGET;
		}

		/**
		 * @eparam CAUSE Clique's ability
		 * @eparam PLAYER you
		 * @eparam TARGET the target of Clique's ability
		 */
		@Override
		public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
		{
			MagicSet cause = parameters.get(Parameter.CAUSE);
			Player you = parameters.get(Parameter.PLAYER).getOne(Player.class);
			Player target = parameters.get(Parameter.TARGET).getOne(Player.class);

			// look at target player's hand.
			Map<Parameter, MagicSet> lookParameters = new HashMap<Parameter, MagicSet>();
			lookParameters.put(Parameter.CAUSE, cause);
			lookParameters.put(Parameter.OBJECT, new MagicSet(target.getHand(game.actualState).objects));
			lookParameters.put(Parameter.PLAYER, new MagicSet(you));
			createEvent(game, "Look at target player's hand", LOOK, lookParameters).perform(event, true);
			you = you.getActual();
			target = target.getActual();

			ParameterMap napParameters = new ParameterMap();
			napParameters.put(Parameter.CAUSE, Identity.instance(cause));
			napParameters.put(Parameter.PLAYER, Identity.instance(you));
			napParameters.put(Parameter.TARGET, Identity.instance(target));

			// You may...
			Map<Parameter, MagicSet> mayParameters = new HashMap<Parameter, MagicSet>();
			mayParameters.put(Parameter.PLAYER, new MagicSet(you));
			mayParameters.put(Parameter.EVENT, new MagicSet(new EventFactory(CARD_NAPPING, napParameters, "Choose a nonland card from it.  That player reveals the chosen card, puts it on the bottom of his or her library, then draws a card.")));
			createEvent(game, "You may choose a nonland card from it.  If you do, that player reveals the chosen card, puts it on the bottom of his or her library, then draws a card.", PLAYER_MAY, mayParameters).perform(event, true);

			event.setResult(Empty.set);
			return true;
		}

	};

	public static final class CardNapper extends EventTriggeredAbility
	{
		public CardNapper(GameState state)
		{
			super(state, "When Vendilion Clique enters the battlefield, look at target player's hand. You may choose a nonland card from it. If you do, that player reveals the chosen card, puts it on the bottom of his or her library, then draws a card.");

			// When Vendilion Clique enters the battlefield,
			this.addPattern(whenThisEntersTheBattlefield());

			Target target = this.addTarget(Players.instance(), "target player");

			EventType.ParameterMap lookChooseParameters = new EventType.ParameterMap();
			lookChooseParameters.put(EventType.Parameter.CAUSE, This.instance());
			lookChooseParameters.put(EventType.Parameter.PLAYER, You.instance());
			lookChooseParameters.put(EventType.Parameter.TARGET, targetedBy(target));
			this.addEffect(new EventFactory(CLIQUE_EFFECT, lookChooseParameters, "Look at target player's hand. You may choose a nonland card from it. If you do, that player reveals the chosen card, puts it on the bottom of his or her library, then draws a card."));
		}
	}

	public VendilionClique(GameState state)
	{
		super(state);

		this.setPower(3);
		this.setToughness(1);

		// Flash
		this.addAbility(new Flash(state));

		// Flying
		this.addAbility(new Flying(state));

		this.addAbility(new CardNapper(state));
	}
}
