package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Transmute;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;
import org.rnd.util.NumberRange;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Name("Dimir Machinations")
@Types({Type.SORCERY})
@ManaCost("2B")
@Printings({@Printings.Printed(ex = Expansion.RAVNICA, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.BLACK})
public final class DimirMachinations extends Card
{
	/**
	 * @eparam CAUSE: Dimir Machinations
	 * @eparam PLAYER: controller of CAUSE
	 * @eparam TARGET: player targetted by CAUSE
	 * @eparam CARD: top three cards of TARGET's library
	 * @eparam RESULT: empty
	 */
	public static final EventType DIMIR_MACHINATIONS_EVENT = new EventType("DIMIR_MACHINATIONS_EVENT")
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
			Player player = parameters.get(Parameter.PLAYER).getOne(Player.class);
			Player target = parameters.get(Parameter.TARGET).getOne(Player.class);

			MagicSet cards = parameters.get(Parameter.CARD);

			Map<Parameter, MagicSet> lookParameters = new HashMap<Parameter, MagicSet>();
			lookParameters.put(Parameter.CAUSE, cause);
			lookParameters.put(EventType.Parameter.OBJECT, cards);
			lookParameters.put(EventType.Parameter.PLAYER, new MagicSet(player));
			createEvent(game, "Look at the top three cards of " + target + "'s library", LOOK, lookParameters).perform(event, true);

			Map<Parameter, MagicSet> exileParameters = new HashMap<Parameter, MagicSet>();
			exileParameters.put(EventType.Parameter.CAUSE, cause);
			exileParameters.put(EventType.Parameter.NUMBER, new MagicSet(new NumberRange(0, cards.size())));
			exileParameters.put(EventType.Parameter.OBJECT, cards);
			exileParameters.put(EventType.Parameter.PLAYER, new MagicSet(player));
			Event exile = createEvent(game, "Exile any number of those cards", EXILE_CHOICE, exileParameters);
			exile.perform(event, true);

			// then put them back in any order.
			if(target.equals(player))
			{
				// If the player looking owns the library being looked at, we
				// can do this the easy way ...
				Map<Parameter, MagicSet> moveParameters = new HashMap<Parameter, MagicSet>();
				moveParameters.put(EventType.Parameter.CAUSE, cause);
				moveParameters.put(EventType.Parameter.INDEX, ONE);
				moveParameters.put(EventType.Parameter.OBJECT, cards);
				Event move = createEvent(game, "Put the rest back in any order.", EventType.PUT_INTO_LIBRARY, moveParameters);
				move.perform(event, false);
			}
			else
			{
				// ... otherwise we'll have to manually ask the player to order
				// the cards.
				player = player.getActual();
				Set<GameObject> choices = new HashSet<GameObject>();
				for(GameObject choice: cards.getAll(GameObject.class))
				{
					GameObject actual = choice.getActual();
					if(!actual.isGhost())
						choices.add(actual);
				}
				List<GameObject> ordered = player.sanitizeAndChoose(game.actualState, choices.size(), choices, PlayerInterface.ChoiceType.MOVEMENT_LIBRARY, PlayerInterface.ChooseReason.ORDER_LIBRARY_TARGET);

				for(GameObject o: ordered)
				{
					Map<Parameter, MagicSet> moveParameters = new HashMap<Parameter, MagicSet>();
					moveParameters.put(EventType.Parameter.CAUSE, cause);
					moveParameters.put(EventType.Parameter.INDEX, ONE);
					moveParameters.put(EventType.Parameter.OBJECT, new MagicSet(o));
					Event move = createEvent(game, "Put a card back.", EventType.PUT_INTO_LIBRARY, moveParameters);
					move.perform(event, true);
				}
			}

			return true;

		}

	};

	public DimirMachinations(GameState state)
	{
		super(state);

		// Look at the top three cards of target player's library. Exile any
		// number of those cards, then put the rest back in any order.
		Target target = this.addTarget(Players.instance(), "target player");

		EventFactory effect = new EventFactory(DIMIR_MACHINATIONS_EVENT, "Look at the top three cards of target player's library. Exile any number of those cards, then put the rest back in any order.");
		effect.parameters.put(EventType.Parameter.CAUSE, This.instance());
		effect.parameters.put(EventType.Parameter.PLAYER, You.instance());
		effect.parameters.put(EventType.Parameter.TARGET, targetedBy(target));
		effect.parameters.put(EventType.Parameter.CARD, TopCards.instance(3, LibraryOf.instance(targetedBy(target))));
		this.addEffect(effect);

		// Transmute (1)(B)(B)
		this.addAbility(new Transmute(state, "(1)(B)(B)"));
	}
}
