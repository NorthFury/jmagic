package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Name("Master of the Wild Hunt")
@Types({Type.CREATURE})
@SubTypes({SubType.HUMAN, SubType.SHAMAN})
@ManaCost("2GG")
@Printings({@Printings.Printed(ex = Expansion.MAGIC_2010, r = Rarity.MYTHIC)})
@ColorIdentity({Color.GREEN})
public final class MasteroftheWildHunt extends Card
{
	// At the beginning of your upkeep, put a 2/2 green Wolf creature token onto
	// the battlefield.
	public static final class MakeWolf extends EventTriggeredAbility
	{
		public MakeWolf(GameState state)
		{
			super(state, "At the beginning of your upkeep, put a 2/2 green Wolf creature token onto the battlefield.");
			this.addPattern(atTheBeginningOfYourUpkeep());

			CreateTokensFactory token = new CreateTokensFactory(1, 2, 2, "Put a 2/2 green Wolf creature token onto the battlefield.");
			token.setColors(Color.GREEN);
			token.setSubTypes(SubType.WOLF);
			this.addEffect(token.getEventFactory());
		}
	}

	/**
	 * @eparam CAUSE: Master of the Wild Hunt's activated ability
	 * @eparam OBJECT: The wolves to tap
	 * @eparam TARGET: The target of CAUSE
	 * @eparam RESULT: empty
	 */
	public static final EventType HUNT_EVENT = new EventType("HUNT_EVENT")
	{
		@Override
		public Parameter affects()
		{
			return null;
		}

		@Override
		public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
		{
			MagicSet cause = parameters.get(Parameter.CAUSE);
			MagicSet wolves = parameters.get(Parameter.OBJECT);

			Map<Parameter, MagicSet> tapParameters = new HashMap<Parameter, MagicSet>();
			tapParameters.put(Parameter.CAUSE, cause);
			tapParameters.put(Parameter.OBJECT, wolves);
			createEvent(game, "Tap all untapped Wolf creatures you control.", TAP_PERMANENTS, tapParameters).perform(event, true);

			MagicSet target = parameters.get(Parameter.TARGET);

			Map<Parameter, MagicSet> biteParameters = new HashMap<Parameter, MagicSet>();
			biteParameters.put(Parameter.SOURCE, wolves);
			biteParameters.put(Parameter.TAKER, target);
			createEvent(game, "Each Wolf tapped this way deals damage equal to its power to target creature.", WOLVES_BITE, biteParameters).perform(event, true);

			Map<Parameter, MagicSet> bittenParameters = new HashMap<Parameter, MagicSet>();
			bittenParameters.put(Parameter.SOURCE, target);
			bittenParameters.put(Parameter.TAKER, wolves);
			createEvent(game, "That creature deals damage equal to its power divided as its controller chooses among any number of those Wolves.", WOLVES_GET_BITTEN, bittenParameters).perform(event, true);

			event.setResult(Empty.set);
			return true;
		}
	};

	/**
	 * @eparam SOURCE: the wolves dealing damage
	 * @eparam TAKER: the target of Master of the Wild Hunt's activated ability
	 * @eparam RESULT: empty
	 */
	public static final EventType WOLVES_BITE = new EventType("WOLVES_BITE")
	{
		@Override
		public Parameter affects()
		{
			return null;
		}

		@Override
		public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
		{
			MagicSet taker = parameters.get(Parameter.TAKER);
			for(GameObject wolf: parameters.get(Parameter.SOURCE).getAll(GameObject.class))
			{
				Map<Parameter, MagicSet> oneWolfDamageParameters = new HashMap<Parameter, MagicSet>();
				oneWolfDamageParameters.put(Parameter.SOURCE, new MagicSet(wolf));
				oneWolfDamageParameters.put(Parameter.TAKER, taker);
				oneWolfDamageParameters.put(Parameter.NUMBER, new MagicSet(wolf.getPower()));
				createEvent(game, wolf + " deals " + wolf.getPower() + " damage to " + taker + ".", DEAL_DAMAGE_EVENLY, oneWolfDamageParameters).perform(event, false);
			}
			event.setResult(Empty.set);
			return true;
		}

	};
	/**
	 * @eparam SOURCE: the creature dealing the damage (target of the activated
	 * ability)
	 * @eparam TAKER: the wolves taking the damage, over which the controller of
	 * SOURCE will divide his creature's damage
	 * @eparam RESULT: empty
	 */
	public static final EventType WOLVES_GET_BITTEN = new EventType("WOLVES_GET_BITTEN")
	{
		@Override
		public Parameter affects()
		{
			return null;
		}

		@Override
		public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
		{
			GameObject dealingBack = parameters.get(Parameter.SOURCE).getOne(GameObject.class);
			Player controller = dealingBack.getController(dealingBack.state);

			List<Target> wolves = new LinkedList<Target>();
			for(GameObject wolf: parameters.get(Parameter.TAKER).getAll(GameObject.class))
				wolves.add(new Target(wolf));
			controller.divide(dealingBack.getPower(), 0, dealingBack.ID, "damage", wolves);

			Map<Parameter, MagicSet> damageParameters = new HashMap<Parameter, MagicSet>();
			damageParameters.put(Parameter.SOURCE, new MagicSet(dealingBack));
			damageParameters.put(Parameter.TAKER, new MagicSet(wolves));
			createEvent(game, dealingBack + " deals damage divided as its controller chooses among " + wolves + ".", DISTRIBUTE_DAMAGE, damageParameters).perform(event, false);

			event.setResult(Empty.set);
			return false;
		}
	};

	public static final class Hunt extends ActivatedAbility
	{
		public Hunt(GameState state)
		{
			super(state, "(T): Tap all untapped Wolf creatures you control. Each Wolf tapped this way deals damage equal to its power to target creature. That creature deals damage equal to its power divided as its controller chooses among any number of those Wolves.");

			this.costsTap = true;

			Target target = this.addTarget(CreaturePermanents.instance(), "target creature");

			SetGenerator wolves = HasSubType.instance(SubType.WOLF);
			SetGenerator wolfCreatures = Intersect.instance(wolves, CreaturePermanents.instance());
			SetGenerator youControl = ControlledBy.instance(You.instance());
			SetGenerator wolfCreaturesYouControl = Intersect.instance(wolfCreatures, youControl);
			SetGenerator tapThese = Intersect.instance(Untapped.instance(), wolfCreaturesYouControl);
			EventType.ParameterMap tapParameters = new EventType.ParameterMap();
			tapParameters.put(EventType.Parameter.CAUSE, This.instance());
			tapParameters.put(EventType.Parameter.OBJECT, tapThese);

			EventType.ParameterMap parameters = new EventType.ParameterMap();
			parameters.put(EventType.Parameter.CAUSE, This.instance());
			parameters.put(EventType.Parameter.OBJECT, tapThese);
			parameters.put(EventType.Parameter.TARGET, targetedBy(target));
			this.addEffect(new EventFactory(HUNT_EVENT, parameters, "Tap all untapped Wolf creatures you control. Each Wolf tapped this way deals damage equal to its power to target creature. That creature deals damage equal to its power divided as its controller chooses among any number of those Wolves."));
		}
	}

	public MasteroftheWildHunt(GameState state)
	{
		super(state);

		this.setPower(3);
		this.setToughness(3);

		this.addAbility(new MakeWolf(state));
		this.addAbility(new Hunt(state));
	}
}
