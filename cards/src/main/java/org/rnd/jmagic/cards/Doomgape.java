package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Trample;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.Map;

@Name("Doomgape")
@Types({Type.CREATURE})
@SubTypes({SubType.ELEMENTAL})
@ManaCost("4(B/G)(B/G)(B/G)")
@Printings({@Printings.Printed(ex = Expansion.EVENTIDE, r = Rarity.RARE)})
@ColorIdentity({Color.GREEN, Color.BLACK})
public final class Doomgape extends Card
{
	public static final class NomNomNom extends EventTriggeredAbility
	{
		/**
		 * @eparam CAUSE: the ability
		 * @eparam PLAYER: the controller of the ability (who is saccing a
		 * creature and gaining life)
		 * @eparam RESULT: empty
		 */
		public static final EventType DOOMGAPE_EVENT = new EventType("DOOMGAPE_EVENT")
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

				Player player = parameters.get(Parameter.PLAYER).getOne(Player.class);

				Map<Parameter, MagicSet> sacParameters = new HashMap<Parameter, MagicSet>();
				sacParameters.put(Parameter.CAUSE, parameters.get(Parameter.CAUSE));
				sacParameters.put(Parameter.NUMBER, ONE);
				sacParameters.put(Parameter.CHOICE, CreaturePermanents.instance().evaluate(game, null));
				sacParameters.put(Parameter.PLAYER, parameters.get(Parameter.PLAYER));
				Event sacEvent = createEvent(game, player + " sacrifices a creature.", EventType.SACRIFICE_CHOICE, sacParameters);
				boolean sacced = sacEvent.perform(event, true);

				GameObject sacrificedCreature = game.actualState.get(sacEvent.getResult().getOne(ZoneChange.class).oldObjectID);

				if(sacced && null != sacrificedCreature)
				{
					Map<Parameter, MagicSet> lifeParameters = new HashMap<Parameter, MagicSet>();
					lifeParameters.put(Parameter.CAUSE, parameters.get(Parameter.CAUSE));
					lifeParameters.put(Parameter.PLAYER, parameters.get(Parameter.PLAYER));
					lifeParameters.put(Parameter.NUMBER, new MagicSet(sacrificedCreature.getToughness()));
					Event lifeEvent = createEvent(game, player + " gains " + sacrificedCreature.getToughness() + " life.", EventType.GAIN_LIFE, lifeParameters);
					lifeEvent.perform(event, true);
				}

				return sacced;
			}

		};

		public NomNomNom(GameState state)
		{
			super(state, "At the beginning of your upkeep, sacrifice a creature. You gain life equal to that creature's toughness.");

			this.addPattern(atTheBeginningOfYourUpkeep());

			EventFactory factory = new EventFactory(DOOMGAPE_EVENT, "Sacrifice a creature. You gain life equal to that creature's toughness.");
			factory.parameters.put(EventType.Parameter.CAUSE, This.instance());
			factory.parameters.put(EventType.Parameter.PLAYER, You.instance());
			this.addEffect(factory);
		}
	}

	public Doomgape(GameState state)
	{
		super(state);

		this.setPower(10);
		this.setToughness(10);

		this.addAbility(new Trample(state));

		this.addAbility(new NomNomNom(state));
	}
}
