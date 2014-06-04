package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.gameTypes.*;
import org.rnd.jmagic.engine.generators.*;
import org.rnd.jmagic.engine.patterns.*;

import java.util.HashMap;
import java.util.Map;

@Name("The \u00C6ther Flues")
@Types({Type.PLANE})
@SubTypes({SubType.IQUATANA})
@Printings({@Printings.Printed(ex = Expansion.PLANECHASE, r = Rarity.COMMON)})
@ColorIdentity({})
public final class TheAEtherFlues extends Card
{
	public static final class TheAvianFlues extends EventTriggeredAbility
	{
		/**
		 * @eparam CAUSE: the cause of this nonsense
		 * @eparam PLAYER: the player making the choice
		 * @eparam RESULT: empty
		 */
		public static final EventType THE_AETHER_FLUES_EVENT = new EventType("THE_AETHER_FLUES_EVENT")
		{
			@Override
			public Parameter affects()
			{
				return null;
			}

			@Override
			public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
			{
				boolean ret;

				Player player = parameters.get(EventType.Parameter.PLAYER).getOne(Player.class);

				MagicSet toReveal = new MagicSet();
				MagicSet toMove = new MagicSet();

				for(GameObject card: player.getLibrary(game.actualState).objects)
				{
					toReveal.add(card);
					if(card.getTypes().contains(Type.CREATURE))
					{
						toMove.add(card);
						break;
					}
				}

				MagicSet cause = parameters.get(EventType.Parameter.CAUSE);
				MagicSet playerSet = new MagicSet(player);

				Map<EventType.Parameter, MagicSet> revealParameters = new HashMap<EventType.Parameter, MagicSet>();
				revealParameters.put(EventType.Parameter.CAUSE, cause);
				revealParameters.put(EventType.Parameter.OBJECT, toReveal);
				Event revealEvent = createEvent(game, "Reveal cards form the top of your library until you reveal a creature card.", EventType.REVEAL, revealParameters);
				ret = revealEvent.perform(event, true);

				Map<EventType.Parameter, MagicSet> moveParameters = new HashMap<EventType.Parameter, MagicSet>();
				moveParameters.put(EventType.Parameter.CAUSE, cause);
				moveParameters.put(EventType.Parameter.CONTROLLER, playerSet);
				moveParameters.put(EventType.Parameter.OBJECT, toMove);
				Event move = createEvent(game, "Put that card onto the battlefield.", EventType.PUT_ONTO_BATTLEFIELD, moveParameters);
				ret = move.perform(event, true) && ret;

				Map<EventType.Parameter, MagicSet> shuffleParameters = new HashMap<EventType.Parameter, MagicSet>();
				shuffleParameters.put(EventType.Parameter.CAUSE, cause);
				shuffleParameters.put(EventType.Parameter.PLAYER, playerSet);
				Event shuffle = createEvent(game, "Shuffle all other cards revealed this way into your library.", EventType.SHUFFLE_LIBRARY, shuffleParameters);
				ret = shuffle.perform(event, true) && ret;

				event.setResult(Empty.set);
				return ret;
			}
		};

		public TheAvianFlues(GameState state)
		{
			super(state, "When you planeswalk to The \u00C6ther Flues or at the beginning of your upkeep, you may sacrifice a creature. If you do, reveal cards from the top of your library until you reveal a creature card, put that card onto the battlefield, then shuffle all other cards revealed this way into your library.");

			SimpleEventPattern pattern = new SimpleEventPattern(Planechase.PLANESWALK);
			pattern.put(EventType.Parameter.PLAYER, You.instance());
			pattern.put(EventType.Parameter.TO, ABILITY_SOURCE_OF_THIS);
			this.addPattern(pattern);
			this.addPattern(atTheBeginningOfYourUpkeep());

			EventFactory then = new EventFactory(THE_AETHER_FLUES_EVENT, "Reveal cards from the top of your library until you reveal a creature card, put that card onto the battlefield, then shuffle all other cards revealed this way into your library.");
			then.parameters.put(EventType.Parameter.CAUSE, This.instance());
			then.parameters.put(EventType.Parameter.PLAYER, You.instance());

			EventFactory factory = new EventFactory(EventType.IF_EVENT_THEN_ELSE, "You may sacrifice a creature. If you do, reveal cards from the top of your library until you reveal a creature card, put that card onto the battlefield, then shuffle all other cards revealed this way into your library.");
			factory.parameters.put(EventType.Parameter.IF, Identity.instance(youMay(sacrificeACreature(), "You may sacrifice a creature.")));
			factory.parameters.put(EventType.Parameter.THEN, Identity.instance(then));
			this.addEffect(factory);

			this.canTrigger = Planechase.triggeredAbilityCanTrigger;
		}
	}

	public static final class TheSwineFlues extends EventTriggeredAbility
	{
		public TheSwineFlues(GameState state)
		{
			super(state, "Whenever you roll (C), you may put a creature card from your hand onto the battlefield.");

			this.addPattern(Planechase.wheneverYouRollChaos());

			EventFactory putOntoBattlefield = new EventFactory(EventType.PUT_ONTO_BATTLEFIELD_CHOICE, "Put a creature card from your hand onto the battlefield.");
			putOntoBattlefield.parameters.put(EventType.Parameter.CAUSE, This.instance());
			putOntoBattlefield.parameters.put(EventType.Parameter.CONTROLLER, You.instance());
			putOntoBattlefield.parameters.put(EventType.Parameter.OBJECT, Intersect.instance(InZone.instance(HandOf.instance(You.instance())), HasType.instance(Type.CREATURE)));
			this.addEffect(youMay(putOntoBattlefield, "You may put a creature card from your hand onto the battlefield."));

			this.canTrigger = Planechase.triggeredAbilityCanTrigger;
		}
	}

	public TheAEtherFlues(GameState state)
	{
		super(state);

		this.addAbility(new TheAvianFlues(state));

		this.addAbility(new TheSwineFlues(state));
	}
}
