package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

@Name("Sphinx of the Chimes")
@Types({Type.CREATURE})
@SubTypes({SubType.SPHINX})
@ManaCost("4UU")
@Printings({@Printings.Printed(ex = Expansion.RETURN_TO_RAVNICA, r = Rarity.RARE)})
@ColorIdentity({Color.BLUE})
public final class SphinxoftheChimes extends Card
{
	/**
	 * @eparam CAUSE: Sphinx of the Chimes' ability
	 * @eparam PLAYER: controller of CAUSE
	 */
	public static final EventType DISCARD_TWO_CARDS_SAME_NAME = new EventType("DISCARD_TWO_CARDS_SAME_NAME")
	{
		@Override
		public Parameter affects()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean attempt(Game game, Event event, Map<Parameter, MagicSet> parameters)
		{
			Player player = parameters.get(Parameter.PLAYER).getOne(Player.class);
			Zone hand = player.getHand(game.actualState);
			if(hand.objects.size() < 2)
				return false;

			MagicSet cause = parameters.get(Parameter.CAUSE);
			ListIterator<GameObject> i = hand.objects.listIterator();
			while(i.hasNext())
			{
				GameObject firstCard = i.next();
				if(firstCard.getTypes().contains(Type.LAND))
					continue;
				ListIterator<GameObject> j = hand.objects.listIterator(i.nextIndex());
				while(j.hasNext())
				{
					GameObject secondCard = j.next();
					if(!firstCard.getName().equals(secondCard.getName()))
						continue;

					Map<Parameter, MagicSet> discardParameters = new HashMap<Parameter, MagicSet>();
					discardParameters.put(Parameter.CAUSE, cause);
					discardParameters.put(Parameter.CARD, new MagicSet(firstCard, secondCard));
					Event discard = createEvent(game, "Discard two cards named " + firstCard, EventType.DISCARD_CARDS, discardParameters);
					if(discard.attempt(event))
						return true;
				}
			}

			return false;
		}

		@Override
		public void makeChoices(Game game, Event event, Map<Parameter, MagicSet> parameters)
		{
			Player player = parameters.get(Parameter.PLAYER).getOne(Player.class);

			if(this.attempt(game, event, parameters))
			{
				boolean valid = false;
				while(!valid)
				{
					List<GameObject> chosen = player.sanitizeAndChoose(game.actualState, 2, player.getHand(game.actualState).objects, PlayerInterface.ChoiceType.OBJECTS, PlayerInterface.ChooseReason.DISCARD);
					if(chosen.get(0).getName().equals(chosen.get(1).getName()) && !chosen.get(0).getTypes().contains(Type.LAND))
					{
						event.putChoices(player, chosen);
						valid = true;
					}
				}
			}
			else
				event.allChoicesMade = false;
		}

		@Override
		public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
		{
			Player you = parameters.get(Parameter.PLAYER).getOne(Player.class);
			MagicSet discardThese = event.getChoices(you);

			Map<Parameter, MagicSet> discardParameters = new HashMap<Parameter, MagicSet>();
			discardParameters.put(Parameter.CAUSE, parameters.get(Parameter.CAUSE));
			discardParameters.put(Parameter.CARD, discardThese);
			Event discard = createEvent(game, "Discard " + discardThese, DISCARD_CARDS, discardParameters);

			boolean status = discard.perform(event, false);
			event.setResult(discard.getResult());
			return status;
		}
	};

	public static final class SphinxoftheChimesAbility1 extends ActivatedAbility
	{
		public SphinxoftheChimesAbility1(GameState state)
		{
			super(state, "Discard two nonland cards with the same name: Draw four cards.");

			// Discard two nonland cards with the same name
			EventFactory cost = new EventFactory(DISCARD_TWO_CARDS_SAME_NAME, "Discard two nonland cards with the same name");
			cost.parameters.put(EventType.Parameter.CAUSE, This.instance());
			cost.parameters.put(EventType.Parameter.PLAYER, You.instance());
			this.addCost(cost);

			this.addEffect(drawCards(You.instance(), 4, "Draw four cards."));
		}
	}

	public SphinxoftheChimes(GameState state)
	{
		super(state);

		this.setPower(5);
		this.setToughness(6);

		// Flying
		this.addAbility(new Flying(state));

		// Discard two nonland cards with the same name: Draw four cards.
		this.addAbility(new SphinxoftheChimesAbility1(state));
	}
}
