package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Name("Sundering Titan")
@Types({Type.ARTIFACT, Type.CREATURE})
@SubTypes({SubType.GOLEM})
@ManaCost("8")
@Printings({@Printings.Printed(ex = Expansion.RELICS, r = Rarity.MYTHIC), @Printings.Printed(ex = Expansion.DARKSTEEL, r = Rarity.RARE)})
@ColorIdentity({})
public final class SunderingTitan extends Card
{
	public static PlayerInterface.ChooseReason SunderingTitanChooseReason(SubType type)
	{
		return new PlayerInterface.ChooseReason("SunderingTitan", "Choose a " + type + " to destroy.", true);
	}

	/**
	 * @eparam CAUSE: what is causing the lands to be destroyed
	 * @eparam PLAYER: who is choosing the lands to be destroyed
	 * @eparam RESULT: the zone changes
	 */
	public static EventType SUNDER = new EventType("SUNDER")
	{
		@Override
		public Parameter affects()
		{
			return Parameter.CHOICE;
		}

		@Override
		public void makeChoices(Game game, Event event, Map<Parameter, MagicSet> parameters)
		{
			for(Player player: parameters.get(Parameter.PLAYER).getAll(Player.class))
			{
				Collection<GameObject> chosenLands = new HashSet<GameObject>();
				for(SubType t: SubType.getBasicLandTypes())
				{
					Collection<GameObject> lands = Intersect.instance(LandPermanents.instance(), HasSubType.instance(t)).evaluate(game, null).getAll(GameObject.class);
					Collection<GameObject> choices = player.sanitizeAndChoose(game.actualState, 1, lands, PlayerInterface.ChoiceType.OBJECTS, SunderingTitanChooseReason(t));
					chosenLands.addAll(choices);
				}
				event.putChoices(player, chosenLands);
			}
			event.allChoicesMade = true;
		}

		@Override
		public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
		{
			boolean allDestroyed = event.allChoicesMade;
			MagicSet cause = parameters.get(Parameter.CAUSE);
			MagicSet result = new MagicSet();

			for(Player player: parameters.get(Parameter.PLAYER).getAll(Player.class))
			{
				MagicSet destroyThese = event.getChoices(player);

				Map<Parameter, MagicSet> destroyParameters = new HashMap<Parameter, MagicSet>();
				destroyParameters.put(Parameter.CAUSE, cause);
				destroyParameters.put(Parameter.PERMANENT, destroyThese);
				Event destroy = createEvent(game, player + " destroys " + destroyThese + ".", DESTROY_PERMANENTS, destroyParameters);
				if(!destroy.perform(event, false))
					allDestroyed = false;
				result.addAll(destroy.getResult());
			}

			event.setResult(Identity.instance(result));
			return allDestroyed;
		}

	};

	public static final class Sunder extends EventTriggeredAbility
	{
		public Sunder(GameState state)
		{
			super(state, "When Sundering Titan enters the battlefield or leaves the battlefield, choose a land of each basic land type, then destroy those lands.");
			this.addPattern(whenThisEntersTheBattlefield());
			this.addPattern(whenThisLeavesTheBattlefield());

			EventFactory sunder = new EventFactory(SUNDER, "Choose a land of each basic land type, then destroy those lands.");
			sunder.parameters.put(EventType.Parameter.CAUSE, This.instance());
			sunder.parameters.put(EventType.Parameter.PLAYER, You.instance());
			this.addEffect(sunder);
		}
	}

	public SunderingTitan(GameState state)
	{
		super(state);

		this.setPower(7);
		this.setToughness(10);

		// When Sundering Titan enters the battlefield or leaves the
		// battlefield, choose a land of each basic land type, then destroy
		// those lands.
		this.addAbility(new Sunder(state));
	}
}
