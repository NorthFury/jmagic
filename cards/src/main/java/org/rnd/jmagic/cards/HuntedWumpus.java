package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Name("Hunted Wumpus")
@Types({Type.CREATURE})
@SubTypes({SubType.BEAST})
@ManaCost("3G")
@Printings({@Printings.Printed(ex = Expansion.TENTH_EDITION, r = Rarity.UNCOMMON), @Printings.Printed(ex = Expansion.NINTH_EDITION, r = Rarity.UNCOMMON), @Printings.Printed(ex = Expansion.EIGHTH_EDITION, r = Rarity.UNCOMMON), @Printings.Printed(ex = Expansion.MERCADIAN_MASQUES, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.GREEN})
public final class HuntedWumpus extends Card
{
	public static final PlayerInterface.ChooseReason REASON = new PlayerInterface.ChooseReason("HuntedWumpus", "Put a creature card from your hand onto the battlefield?", true);

	public static final class WumpusMeat extends EventTriggeredAbility
	{
		/**
		 * @eparam CAUSE: The Wumpus' ability
		 * @eparam PLAYER: The players who will be given the choice
		 * @eparam CHOICE: The filter to run against the players hand
		 * @eparam RESULT: empty
		 */
		public final static EventType HUNTED_WUMPUS_EVENT = new EventType("HUNTED_WUMPUS_EVENT")
		{
			@Override
			public Parameter affects()
			{
				return null;
			}

			@Override
			public void makeChoices(Game game, Event event, Map<Parameter, MagicSet> parameters)
			{
				MagicSet filter = parameters.get(EventType.Parameter.CHOICE);

				for(Player player: parameters.get(EventType.Parameter.PLAYER).getAll(Player.class))
				{
					MagicSet allowedCards = Intersect.get(filter, new MagicSet(player.getHand(game.actualState).objects));
					if(allowedCards.isEmpty())
					{
						// They can't put a card onto the battlefield if they
						// don't have one
						event.putChoices(player, Collections.emptySet());
						continue;
					}

					List<Answer> yesNo = player.sanitizeAndChoose(game.actualState, 1, Answer.mayChoices(), PlayerInterface.ChoiceType.ENUM, REASON);
					if(yesNo.iterator().next() == Answer.NO)
						// They won't put a card onto the battlefield if they
						// choose not to
						continue;

					// Choose a card to put onto the battlefield
					List<GameObject> choice = player.sanitizeAndChoose(game.actualState, 1, allowedCards.getAll(GameObject.class), PlayerInterface.ChoiceType.OBJECTS, PlayerInterface.ChooseReason.PUT_ONTO_BATTLEFIELD);
					event.putChoices(player, choice);
				}
			}

			@Override
			public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
			{
				MagicSet objects = new MagicSet();
				for(Player player: parameters.get(Parameter.PLAYER).getAll(Player.class))
					objects.addAll(event.getChoices(player));

				Map<Parameter, MagicSet> moveParameters = new HashMap<Parameter, MagicSet>();
				moveParameters.put(Parameter.CAUSE, parameters.get(Parameter.CAUSE));
				moveParameters.put(Parameter.OBJECT, objects);
				Event putOntoField = createEvent(game, "Put the chosen cards onto the battlefield under their owners' control.", EventType.PUT_ONTO_BATTLEFIELD_UNDER_OWNER_CONTROL, moveParameters);
				putOntoField.perform(event, true);

				event.setResult(Empty.set);
				return true;
			}
		};

		public WumpusMeat(GameState state)
		{
			super(state, "When Hunted Wumpus enters the battlefield, each other player may put a creature card from his or her hand onto the battlefield.");
			this.addPattern(whenThisEntersTheBattlefield());

			EventType.ParameterMap parameters = new EventType.ParameterMap();
			parameters.put(EventType.Parameter.CAUSE, This.instance());
			parameters.put(EventType.Parameter.PLAYER, RelativeComplement.instance(Players.instance(), You.instance()));
			parameters.put(EventType.Parameter.CHOICE, HasType.instance(Type.CREATURE));
			this.addEffect(new EventFactory(HUNTED_WUMPUS_EVENT, parameters, "Each other player may put a creature card rom his or her hand onto the battlefield."));
		}
	}

	public HuntedWumpus(GameState state)
	{
		super(state);

		this.setPower(6);
		this.setToughness(6);

		this.addAbility(new WumpusMeat(state));
	}
}
