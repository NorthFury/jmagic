package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

@Name("Thought Hemorrhage")
@Types({Type.SORCERY})
@ManaCost("2BR")
@Printings({@Printings.Printed(ex = Expansion.ALARA_REBORN, r = Rarity.RARE)})
@ColorIdentity({Color.BLACK, Color.RED})
public final class ThoughtHemorrhage extends Card
{
	/**
	 * @eparam CAUSE: thought hemorrhage
	 * @eparam PLAYER: controller of CAUSE
	 * @eparam TARGET: target of CAUSE
	 */
	public static final EventType THOUGHT_HEMORRHAGE_EVENT = new EventType("THOUGHT_HEMORRHAGE_EVENT")
	{
		@Override
		public Parameter affects()
		{
			return null;
		}

		@Override
		public boolean perform(Game game, Event event, java.util.Map<Parameter, MagicSet> parameters)
		{
			Player you = parameters.get(Parameter.PLAYER).getOne(Player.class);
			String chosenName = you.choose(1, NonLandCardNames.get().getAll(String.class), PlayerInterface.ChoiceType.STRING, PlayerInterface.ChooseReason.NAME_A_NONLAND_CARD).get(0);

			MagicSet cause = parameters.get(Parameter.CAUSE);

			Player target = parameters.get(Parameter.TARGET).getOne(Player.class).getActual();
			MagicSet cardsInTargetsHand = new MagicSet(target.getHand(game.actualState).objects);

			java.util.Map<Parameter, MagicSet> revealParameters = new java.util.HashMap<Parameter, MagicSet>();
			revealParameters.put(Parameter.CAUSE, cause);
			revealParameters.put(Parameter.OBJECT, cardsInTargetsHand);
			Event reveal = createEvent(game, "Target player reveals his or her hand", REVEAL, revealParameters);
			reveal.perform(event, true);

			target = target.getActual();
			int amount = 0;
			for(GameObject object: target.getHand(game.actualState).objects)
				if(object.getName().equals(chosenName))
					amount += 3;

			java.util.Map<Parameter, MagicSet> damageParameters = new java.util.HashMap<Parameter, MagicSet>();
			damageParameters.put(Parameter.SOURCE, cause);
			damageParameters.put(Parameter.NUMBER, new MagicSet(amount));
			damageParameters.put(Parameter.TAKER, new MagicSet(target));
			Event damage = createEvent(game, "Thought Hemorrhage deals 3 damage to that player for each card with that name revealed this way", DEAL_DAMAGE_EVENLY, damageParameters);
			damage.perform(event, true);

			target = target.getActual();
			java.util.Map<Parameter, MagicSet> searchParameters = new java.util.HashMap<Parameter, MagicSet>();
			searchParameters.put(Parameter.CAUSE, cause);
			searchParameters.put(Parameter.PLAYER, new MagicSet(you));
			searchParameters.put(Parameter.NUMBER, new MagicSet(new org.rnd.util.NumberRange(0, null)));
			searchParameters.put(Parameter.CARD, new MagicSet(target.getGraveyard(game.actualState), target.getHand(game.actualState), target.getLibrary(game.actualState)));
			searchParameters.put(Parameter.TYPE, new MagicSet(HasName.instance(chosenName)));
			Event search = createEvent(game, "Search that player's graveyard, hand, and library for all cards with that name", SEARCH, searchParameters);
			search.perform(event, true);

			java.util.Map<Parameter, MagicSet> exileParameters = new java.util.HashMap<Parameter, MagicSet>();
			exileParameters.put(Parameter.CAUSE, cause);
			exileParameters.put(Parameter.TO, new MagicSet(game.actualState.exileZone()));
			exileParameters.put(Parameter.OBJECT, search.getResult());
			Event exile = createEvent(game, "Exile those cards", MOVE_OBJECTS, exileParameters);
			exile.perform(event, false);

			java.util.Map<Parameter, MagicSet> shuffleParameters = new java.util.HashMap<Parameter, MagicSet>();
			shuffleParameters.put(Parameter.CAUSE, cause);
			shuffleParameters.put(Parameter.PLAYER, new MagicSet(target));
			Event shuffle = createEvent(game, "That player shuffles his or her library", SHUFFLE_LIBRARY, shuffleParameters);
			shuffle.perform(event, true);

			event.setResult(Empty.set);
			return true;
		}
	};

	public ThoughtHemorrhage(GameState state)
	{
		super(state);

		Target target = this.addTarget(Players.instance(), "target permanent");

		// Name a nonland card. Target player reveals his or her hand. Thought
		// Hemorrhage deals 3 damage to that player for each card with that name
		// revealed this way. Search that player's graveyard, hand, and library
		// for all cards with that name and exile them. Then that player
		// shuffles his or her library.
		EventFactory effect = new EventFactory(THOUGHT_HEMORRHAGE_EVENT, "Name a nonland card. Target player reveals his or her hand. Thought Hemorrhage deals 3 damage to that player for each card with that name revealed this way. Search that player's graveyard, hand, and library for all cards with that name and exile them. Then that player shuffles his or her library.");
		effect.parameters.put(EventType.Parameter.CAUSE, This.instance());
		effect.parameters.put(EventType.Parameter.PLAYER, You.instance());
		effect.parameters.put(EventType.Parameter.TARGET, targetedBy(target));
		this.addEffect(effect);
	}
}
