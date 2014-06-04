package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class AttachToChoice extends EventType
{	public static final EventType INSTANCE = new AttachToChoice();

	 private AttachToChoice()
	{
		super("ATTACH_TO_CHOICE");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.CHOICE;
	}

	@Override
	public boolean attempt(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		MagicSet object = parameters.get(Parameter.OBJECT);
		MagicSet choices = parameters.get(Parameter.CHOICE);
		Player player = parameters.get(Parameter.PLAYER).getOne(Player.class);

		// reset this players successes to zero
		MagicSet failedCards = new MagicSet();

		while(true)
		{
			if(choices.isEmpty())
				return false;

			GameObject thisCard = choices.getOne(GameObject.class);
			choices.remove(thisCard);
			Map<Parameter, MagicSet> newParameters = new HashMap<Parameter, MagicSet>();
			parameters.put(Parameter.OBJECT, object);
			parameters.put(Parameter.TARGET, new MagicSet(thisCard));

			// if the player can attach the card, go to next player.
			// otherwise, give other players the chance to attach the
			// card.
			if(createEvent(game, player + " attaches " + thisCard + " to " + object + ".", ATTACH, newParameters).attempt(event))
				break;
			failedCards.add(thisCard);
		}

		// next player is given the chance to choose all cards this
		// player failed on
		choices.addAll(failedCards);

		return true;
	}

	@Override
	public void makeChoices(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		Set<AttachableTo> validChoices = parameters.get(Parameter.CHOICE).getAll(AttachableTo.class);
		GameObject object = parameters.get(Parameter.OBJECT).getOne(GameObject.class);
		Player player = parameters.get(Parameter.PLAYER).getOne(Player.class);

		Iterator<AttachableTo> i = validChoices.iterator();
		while(i.hasNext())
		{
			AttachableTo to = i.next();
			Map<Parameter, MagicSet> attachParameters = new HashMap<Parameter, MagicSet>();
			attachParameters.put(Parameter.OBJECT, new MagicSet(object));
			attachParameters.put(Parameter.TARGET, new MagicSet(to));
			Event attachEvent = createEvent(game, player + " attaches " + object + " to " + to + ".", ATTACH, attachParameters);
			if(!attachEvent.attempt(event))
				i.remove();
		}

		PlayerInterface.ChooseParameters<Serializable> chooseParameters = new PlayerInterface.ChooseParameters<Serializable>(1, 1, PlayerInterface.ChoiceType.OBJECTS, PlayerInterface.ChooseReason.ATTACH);
		chooseParameters.thisID = object.ID;
		Collection<AttachableTo> choices = player.sanitizeAndChoose(game.actualState, validChoices, chooseParameters);
		if(choices.isEmpty())
			event.allChoicesMade = false;
		event.putChoices(player, choices);
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		boolean ret = event.allChoicesMade;
		MagicSet result = new MagicSet();
		MagicSet object = parameters.get(Parameter.OBJECT);
		Player player = parameters.get(Parameter.PLAYER).getOne(Player.class);

		MagicSet choices = event.getChoices(player);
		if(!choices.isEmpty())
		{
			// perform the attach event
			Map<Parameter, MagicSet> attachParameters = new HashMap<Parameter, MagicSet>();
			attachParameters.put(Parameter.OBJECT, object);
			attachParameters.put(Parameter.TARGET, choices);
			Event attachEvent = createEvent(game, player + " attaches " + object + " to " + choices + ".", ATTACH, attachParameters);
			if(!attachEvent.perform(event, false))
				ret = false;
			result.addAll(attachEvent.getResult());
		}

		event.setResult(Identity.instance(result));
		return ret;
	}
}