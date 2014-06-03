package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

public final class DiscardChoice extends EventType
{	public static final EventType INSTANCE = new DiscardChoice();

	 private DiscardChoice()
	{
		super("DISCARD_CHOICE");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.CHOICE;
	}

	@Override
	public boolean attempt(Game game, Event event, java.util.Map<Parameter, MagicSet> parameters)
	{
		int successes = 0;
		MagicSet cause = parameters.get(Parameter.CAUSE);
		int required = 1;
		if(parameters.containsKey(Parameter.NUMBER))
			required = Sum.get(parameters.get(Parameter.NUMBER));

		for(Player player: parameters.get(Parameter.PLAYER).getAll(Player.class))
		{
			java.util.Set<Card> cards = null;
			if(parameters.containsKey(Parameter.CHOICE))
				cards = parameters.get(Parameter.CHOICE).getAll(Card.class);
			else
				cards = new MagicSet(player.getHand(game.actualState).objects).getAll(Card.class);

			successes = 0;
			for(Card thisCard: cards)
			{
				java.util.Map<Parameter, MagicSet> newParameters = new java.util.HashMap<Parameter, MagicSet>();
				newParameters.put(Parameter.CAUSE, cause);
				newParameters.put(Parameter.CARD, new MagicSet(thisCard));
				if(createEvent(game, thisCard.getOwner(game.actualState) + " discards " + thisCard + ".", DISCARD_ONE_CARD, newParameters).attempt(event))
					successes++;

				if(successes == required)
					break;
			}
			if(successes != required)
				return false;
		}
		return true;
	}

	@Override
	public void makeChoices(Game game, Event event, java.util.Map<Parameter, MagicSet> parameters)
	{
		int numberOfCards = 1;
		if(parameters.containsKey(Parameter.NUMBER))
			numberOfCards = Sum.get(parameters.get(Parameter.NUMBER));

		if(numberOfCards < 0)
			numberOfCards = 0;

		java.util.Set<Card> cardsInHand = null;
		boolean specificChoices = parameters.containsKey(Parameter.CHOICE);

		if(specificChoices)
			cardsInHand = parameters.get(Parameter.CHOICE).getAll(Card.class);

		for(Player player: parameters.get(Parameter.PLAYER).getAll(Player.class))
		{
			if(!specificChoices)
				cardsInHand = new MagicSet(player.getHand(game.actualState).objects).getAll(Card.class);

			java.util.Collection<Card> choices = player.sanitizeAndChoose(game.actualState, numberOfCards, cardsInHand, PlayerInterface.ChoiceType.OBJECTS, PlayerInterface.ChooseReason.DISCARD);
			if(choices.size() != numberOfCards)
				event.allChoicesMade = false;
			event.putChoices(player, choices);
		}
	}

	@Override
	public boolean perform(Game game, Event event, java.util.Map<Parameter, MagicSet> parameters)
	{
		boolean allDiscarded = event.allChoicesMade;
		MagicSet cause = parameters.get(Parameter.CAUSE);
		MagicSet result = new MagicSet();

		for(Player player: parameters.get(Parameter.PLAYER).getAll(Player.class))
		{
			MagicSet discardThese = event.getChoices(player);

			java.util.Map<Parameter, MagicSet> discardParameters = new java.util.HashMap<Parameter, MagicSet>();
			discardParameters.put(Parameter.CAUSE, cause);
			discardParameters.put(Parameter.CARD, discardThese);
			Event discard = createEvent(game, player + " discards " + discardThese + ".", DISCARD_CARDS, discardParameters);
			if(!discard.perform(event, false))
				allDiscarded = false;
			result.addAll(discard.getResult());
		}

		event.setResult(Identity.instance(result));
		return allDiscarded;
	}
}