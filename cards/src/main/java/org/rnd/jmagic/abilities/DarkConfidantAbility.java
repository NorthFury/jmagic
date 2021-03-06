package org.rnd.jmagic.abilities;

import static org.rnd.jmagic.Convenience.*;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.Map;

public final class DarkConfidantAbility extends EventTriggeredAbility
{
	/**
	 * @eparam CAUSE: Dark Confidant's triggered ability
	 * @eparam PLAYER: CAUSE's controller
	 * @eparam TO: PLAYER's hand
	 * @eparam OBJECT: the top card of FROM
	 * @eparam RESULT: empty
	 */
	public static EventType CONFIDANT_EVENT = new EventType("CONFIDANT_EVENT")
	{
		@Override
		public Parameter affects()
		{
			return null;
		}

		@Override
		public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
		{
			MagicSet object = parameters.get(Parameter.OBJECT);
			event.setResult(Empty.set);
			if(object.isEmpty())
				return false;

			MagicSet cause = parameters.get(Parameter.CAUSE);
			MagicSet player = parameters.get(Parameter.PLAYER);
			MagicSet to = parameters.get(Parameter.TO);
			int life = object.getOne(GameObject.class).getConvertedManaCost();

			Map<Parameter, MagicSet> revealParameters = new HashMap<Parameter, MagicSet>();
			revealParameters.put(Parameter.CAUSE, cause);
			revealParameters.put(Parameter.OBJECT, object);
			createEvent(game, "Reveal the top card of your library", REVEAL, revealParameters).perform(event, true);

			Map<Parameter, MagicSet> moveParameters = new HashMap<Parameter, MagicSet>();
			moveParameters.put(Parameter.CAUSE, cause);
			moveParameters.put(Parameter.OBJECT, object);
			moveParameters.put(Parameter.TO, to);
			createEvent(game, "Put that card into your hand", MOVE_OBJECTS, moveParameters).perform(event, true);

			Map<Parameter, MagicSet> lifeParameters = new HashMap<Parameter, MagicSet>();
			lifeParameters.put(Parameter.CAUSE, cause);
			lifeParameters.put(Parameter.PLAYER, player);
			lifeParameters.put(Parameter.NUMBER, new MagicSet(life));
			createEvent(game, "You lose life equal to its converted mana cost", LOSE_LIFE, lifeParameters).perform(event, true);

			return true;
		}
	};

	public DarkConfidantAbility(GameState state)
	{
		super(state, "At the beginning of your upkeep, reveal the top card of your library and put that card into your hand. You lose life equal to its converted mana cost.");
		this.addPattern(atTheBeginningOfYourUpkeep());

		SetGenerator library = LibraryOf.instance(You.instance());
		SetGenerator topCard = TopCards.instance(numberGenerator(1), library);

		EventType.ParameterMap parameters = new EventType.ParameterMap();
		parameters.put(EventType.Parameter.CAUSE, This.instance());
		parameters.put(EventType.Parameter.TO, HandOf.instance(You.instance()));
		parameters.put(EventType.Parameter.OBJECT, topCard);
		parameters.put(EventType.Parameter.PLAYER, You.instance());
		this.addEffect(new EventFactory(CONFIDANT_EVENT, parameters, "Reveal the top card of your library and put that card into your hand. You lose life equal to its converted mana cost."));

	}
}