package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.Map;

@Name("Corpse Harvester")
@Types({Type.CREATURE})
@SubTypes({SubType.ZOMBIE, SubType.WIZARD})
@ManaCost("3BB")
@Printings({@Printings.Printed(ex = Expansion.LEGIONS, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.BLACK})
public final class CorpseHarvester extends Card
{
	/**
	 * @eparam CAUSE: corpse harvester's ability
	 * @eparam PLAYER: controller of CAUSE
	 */
	public static final EventType CORPSE_HARVESTER_EVENT = new EventType("CORPSE_HARVESTER_EVENT")
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
			Player you = parameters.get(Parameter.PLAYER).getOne(Player.class);
			Zone library = you.getLibrary(game.actualState);

			Map<Parameter, MagicSet> zombieParameters = new HashMap<Parameter, MagicSet>();
			zombieParameters.put(Parameter.CAUSE, cause);
			zombieParameters.put(Parameter.PLAYER, new MagicSet(you));
			zombieParameters.put(Parameter.NUMBER, ONE);
			zombieParameters.put(Parameter.CARD, new MagicSet(library));
			zombieParameters.put(Parameter.TYPE, new MagicSet(HasSubType.instance(SubType.ZOMBIE)));
			Event zombieSearch = createEvent(game, "Search your library for a Zombie card", SEARCH, zombieParameters);
			zombieSearch.perform(event, false);

			Map<Parameter, MagicSet> swampParameters = new HashMap<Parameter, MagicSet>(zombieParameters);
			swampParameters.put(Parameter.TYPE, new MagicSet(HasSubType.instance(SubType.SWAMP)));
			Event swampSearch = createEvent(game, "Search your library for a Swamp card", SEARCH, swampParameters);
			swampSearch.perform(event, false);

			MagicSet thoseCards = new MagicSet();
			thoseCards.addAll(zombieSearch.getResult());
			thoseCards.addAll(swampSearch.getResult());

			Map<Parameter, MagicSet> moveParameters = new HashMap<Parameter, MagicSet>();
			moveParameters.put(Parameter.CAUSE, cause);
			moveParameters.put(Parameter.TO, new MagicSet(you.getHand(game.actualState)));
			moveParameters.put(Parameter.OBJECT, thoseCards);
			Event move = createEvent(game, "Put those cards into your hand", MOVE_OBJECTS, moveParameters);
			move.perform(event, true);

			Map<Parameter, MagicSet> shuffleParameters = new HashMap<Parameter, MagicSet>();
			shuffleParameters.put(Parameter.CAUSE, cause);
			shuffleParameters.put(Parameter.PLAYER, new MagicSet(you));
			Event shuffle = createEvent(game, "Shuffle your library", SHUFFLE_LIBRARY, shuffleParameters);
			shuffle.perform(event, true);

			return true;
		}
	};

	public static final class CorpseHarvesterAbility0 extends ActivatedAbility
	{
		public CorpseHarvesterAbility0(GameState state)
		{
			super(state, "(1)(B), (T), Sacrifice a creature: Search your library for a Zombie card and a Swamp card, reveal them, and put them into your hand. Then shuffle your library.");
			this.setManaCost(new ManaPool("(1)(B)"));
			this.costsTap = true;
			this.addCost(sacrificeACreature());

			EventFactory complicatedSearch = new EventFactory(CORPSE_HARVESTER_EVENT, "Search your library for a Zombie card and a Swamp card, reveal them, and put them into your hand. Then shuffle your library.");
			complicatedSearch.parameters.put(EventType.Parameter.CAUSE, This.instance());
			complicatedSearch.parameters.put(EventType.Parameter.PLAYER, You.instance());
			this.addEffect(complicatedSearch);
		}
	}

	public CorpseHarvester(GameState state)
	{
		super(state);

		this.setPower(3);
		this.setToughness(3);

		// (1)(B), (T), Sacrifice a creature: Search your library for a Zombie
		// card and a Swamp card, reveal them, and put them into your hand. Then
		// shuffle your library.
		this.addAbility(new CorpseHarvesterAbility0(state));
	}
}
