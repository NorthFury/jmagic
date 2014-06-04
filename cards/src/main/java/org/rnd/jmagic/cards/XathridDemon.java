package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Flying;
import org.rnd.jmagic.abilities.keywords.Trample;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.Map;

@Name("Xathrid Demon")
@Types({Type.CREATURE})
@SubTypes({SubType.DEMON})
@ManaCost("3BBB")
@Printings({@Printings.Printed(ex = Expansion.MAGIC_2010, r = Rarity.MYTHIC)})
@ColorIdentity({Color.BLACK})
public final class XathridDemon extends Card
{
	public static final class XathridSac extends EventTriggeredAbility
	{
		/**
		 * @eparam CAUSE: the XathridSac ability
		 * @eparam RESULT: empty
		 */
		public static final EventType XATHRID_DEMON_EVENT = new EventType("XATHRID_DEMON_EVENT")
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

				MagicSet cause = parameters.get(EventType.Parameter.CAUSE);
				EventTriggeredAbility ability = cause.getOne(EventTriggeredAbility.class);
				GameObject creature = (GameObject)(ability.getSource(game.actualState));
				Player controller = ability.getController(ability.state);

				Map<EventType.Parameter, MagicSet> sacParameters = new HashMap<EventType.Parameter, MagicSet>();
				sacParameters.put(EventType.Parameter.CAUSE, cause);
				sacParameters.put(EventType.Parameter.NUMBER, ONE);
				sacParameters.put(EventType.Parameter.CHOICE, RelativeComplement.instance(ControlledBy.instance(Identity.instance(controller)), Identity.instance(creature)).evaluate(game, null));
				sacParameters.put(EventType.Parameter.PLAYER, new MagicSet(controller));
				Event sacEvent = createEvent(game, "Sacrifice a creature other than Xathrid Demon.", EventType.SACRIFICE_CHOICE, sacParameters);

				Map<EventType.Parameter, MagicSet> lifeParameters = new HashMap<EventType.Parameter, MagicSet>();
				lifeParameters.put(EventType.Parameter.CAUSE, cause);

				if(sacEvent.perform(event, true))
				{
					MagicSet sacrificed = OldObjectOf.instance(sacEvent.getResultGenerator()).evaluate(game.actualState, null);

					lifeParameters.put(EventType.Parameter.PLAYER, OpponentsOf.get(game.physicalState, controller));
					lifeParameters.put(EventType.Parameter.NUMBER, new MagicSet(sacrificed.getOne(GameObject.class).getPower()));
					Event lifeEvent = createEvent(game, "Each opponent loses life equal to the sacrificed creature's power.", EventType.LOSE_LIFE, lifeParameters);
					return lifeEvent.perform(event, false);
				}

				Map<EventType.Parameter, MagicSet> tapParameters = new HashMap<EventType.Parameter, MagicSet>();
				tapParameters.put(EventType.Parameter.CAUSE, cause);
				tapParameters.put(EventType.Parameter.OBJECT, new MagicSet(creature));
				Event tapEvent = createEvent(game, "Tap Xathrid Demon.", EventType.TAP_PERMANENTS, tapParameters);
				boolean tapped = tapEvent.perform(event, false);

				lifeParameters.put(EventType.Parameter.PLAYER, new MagicSet(controller));
				lifeParameters.put(EventType.Parameter.NUMBER, new MagicSet(7));
				Event lifeEvent = createEvent(game, "You lose 7 life.", EventType.LOSE_LIFE, lifeParameters);
				return lifeEvent.perform(event, false) && tapped;
			}
		};

		public XathridSac(GameState state)
		{
			super(state, "At the beginning of your upkeep, sacrifice a creature other than Xathrid Demon, then each opponent loses life equal to the sacrificed creature's power. If you can't sacrifice a creature, tap Xathrid Demon and you lose 7 life.");

			this.addPattern(atTheBeginningOfYourUpkeep());

			EventType.ParameterMap parameters = new EventType.ParameterMap();
			parameters.put(EventType.Parameter.CAUSE, This.instance());
			this.addEffect(new EventFactory(XATHRID_DEMON_EVENT, parameters, "Sacrifice a creature other than Xathrid Demon, then each opponent loses life equal to the sacrificed creature's power. If you can't sacrifice a creature, tap Xathrid Demon and you lose 7 life."));
		}
	}

	public XathridDemon(GameState state)
	{
		super(state);

		this.setPower(7);
		this.setToughness(7);

		this.addAbility(new Flying(state));
		this.addAbility(new Trample(state));
		this.addAbility(new XathridSac(state));
	}
}
