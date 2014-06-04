package org.rnd.jmagic.cards;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.Map;

@Name("Kodama's Reach")
@Types({Type.SORCERY})
@SubTypes({SubType.ARCANE})
@ManaCost("2G")
@Printings({@Printings.Printed(ex = Expansion.COMMANDER, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.CHAMPIONS_OF_KAMIGAWA, r = Rarity.COMMON)})
@ColorIdentity({Color.GREEN})
public final class KodamasReach extends Card
{
	/**
	 * @eparam CAUSE: Kodama's Reach
	 * @eparam PLAYER: controller of CAUSE
	 * @eparam CHOICE: generator evaluating to the basic land cards in PLAYER's
	 * library (requires double generator idiom)
	 */
	public static final EventType KODAMAS_REACH_EVENT = new EventType("KODAMAS_REACH_EVENT")
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

			MagicSet cause = parameters.get(Parameter.CAUSE);
			Player you = parameters.get(Parameter.PLAYER).getOne(Player.class);
			MagicSet choices = parameters.get(Parameter.CHOICE);

			Map<Parameter, MagicSet> searchParameters = new HashMap<Parameter, MagicSet>();
			searchParameters.put(Parameter.CAUSE, cause);
			searchParameters.put(Parameter.PLAYER, new MagicSet(you));
			searchParameters.put(Parameter.NUMBER, new MagicSet(2));
			searchParameters.put(Parameter.CARD, new MagicSet(you.getLibrary(game.actualState).objects));
			searchParameters.put(Parameter.TYPE, choices);
			Event search = createEvent(game, "Search your library for two basic land cards and reveal those cards", EventType.SEARCH, searchParameters);
			search.perform(event, true);
			MagicSet found = search.getResult();

			you = you.getActual();
			MagicSet chosen = new MagicSet(you.sanitizeAndChoose(game.actualState, 1, found.getAll(GameObject.class), PlayerInterface.ChoiceType.OBJECTS, PlayerInterface.ChooseReason.PUT_ONTO_BATTLEFIELD));

			Map<Parameter, MagicSet> battlefieldParameters = new HashMap<Parameter, MagicSet>();
			battlefieldParameters.put(Parameter.CAUSE, cause);
			battlefieldParameters.put(Parameter.CONTROLLER, new MagicSet(you));
			battlefieldParameters.put(Parameter.OBJECT, chosen);
			Event putOntoBattlefield = createEvent(game, "Put one onto the battlefield tapped", EventType.PUT_ONTO_BATTLEFIELD_TAPPED, battlefieldParameters);
			putOntoBattlefield.perform(event, false);

			you = you.getActual();
			Map<Parameter, MagicSet> handParameters = new HashMap<Parameter, MagicSet>();
			handParameters.put(Parameter.CAUSE, cause);
			handParameters.put(Parameter.TO, new MagicSet(you.getHand(game.actualState)));
			handParameters.put(Parameter.OBJECT, found);
			Event putIntoHand = createEvent(game, "Put one the other into your hand", EventType.MOVE_OBJECTS, handParameters);
			putIntoHand.perform(event, false);

			return true;
		}
	};

	public KodamasReach(GameState state)
	{
		super(state);

		// Search your library for two basic land cards, reveal those cards, and
		// put one onto the battlefield tapped and the other into your hand.
		// Then shuffle your library.
		SetGenerator basicLandCards = Intersect.instance(HasSuperType.instance(SuperType.BASIC), HasType.instance(Type.LAND));
		SetGenerator basicLandsInYourLibrary = Intersect.instance(InZone.instance(LibraryOf.instance(You.instance())), basicLandCards);

		EventFactory effect = new EventFactory(KODAMAS_REACH_EVENT, "Search your library for two basic land cards, reveal those cards, and put one onto the battlefield tapped and the other into your hand. Then shuffle your library.");
		effect.parameters.put(EventType.Parameter.CAUSE, This.instance());
		effect.parameters.put(EventType.Parameter.PLAYER, You.instance());
		effect.parameters.put(EventType.Parameter.CHOICE, Identity.instance(basicLandsInYourLibrary));
		this.addEffect(effect);
	}
}
