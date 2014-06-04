package org.rnd.jmagic.engine.eventTypes;

import org.rnd.jmagic.abilities.keywords.Deathtouch;
import org.rnd.jmagic.abilities.keywords.Infect;
import org.rnd.jmagic.abilities.keywords.Lifelink;
import org.rnd.jmagic.abilities.keywords.Wither;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class DealDamageBatches extends EventType
{	public static final EventType INSTANCE = new DealDamageBatches();

	 private DealDamageBatches()
	{
		super("DEAL_DAMAGE_BATCHES");
	}

	@Override
	public Parameter affects()
	{
		return Parameter.TARGET;
	}

	private boolean fakeAbilityForDamage(DamageAssignment.Batch damage, Class<? extends Keyword> ability, GameState state)
	{
		for(Map.Entry<Integer, ContinuousEffectType.DamageAbility> entry: state.dealDamageAsThoughHasAbility.entrySet())
			if(!entry.getValue().dp.match(damage, state.get(entry.getKey()), state).isEmpty())
				if(entry.getValue().k.isAssignableFrom(ability))
					return true;
		return false;
	}

	@Override
	public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
	{
		// the structure of these maps is:
		// map<player, map<source, amount>>
		Map<Player, Map<GameObject, Integer>> lifeLosses = new HashMap<Player, Map<GameObject, Integer>>();
		Map<Player, Map<GameObject, Integer>> lifeGains = new HashMap<Player, Map<GameObject, Integer>>();
		// the maps are split up in this manner because of this rule:
		// 118.9. Some triggered abilities are written, "Whenever [a player]
		// gains life, . . . ." Such abilities are treated as though they
		// are written, "Whenever a source causes [a player] to gain life, .
		// . . ."

		Map<GameObject, Integer> creaturesGettingCounters = new HashMap<GameObject, Integer>();
		Map<Player, Integer> playersGettingPoisonCounters = new HashMap<Player, Integer>();
		Map<GameObject, Integer> planeswalkersLosingCounters = new HashMap<GameObject, Integer>();

		Set<DamageAssignment> assignments = parameters.get(Parameter.TARGET).getAll(DamageAssignment.class);
		for(DamageAssignment assignment: assignments)
		{
			// for checking as-though effects:
			DamageAssignment.Batch batch = new DamageAssignment.Batch();
			batch.add(assignment);

			GameObject source = game.actualState.get(assignment.sourceID);
			Identified taker = game.actualState.get(assignment.takerID);

			boolean lifelink = source.hasAbility(Lifelink.class);
			if(!lifelink)
				lifelink = fakeAbilityForDamage(batch, Lifelink.class, game.actualState);
			if(lifelink)
			{
				Player controller = source.getController(source.state);
				if(lifeGains.containsKey(controller))
				{
					Map<GameObject, Integer> lifeGain = lifeGains.get(controller);
					if(lifeGain.containsKey(source))
						lifeGain.put(source, lifeGain.get(source) + 1);
					else
						lifeGain.put(source, 1);
				}
				else
				{
					Map<GameObject, Integer> lifeGain = new HashMap<GameObject, Integer>();
					lifeGain.put(source, 1);
					lifeGains.put(controller, lifeGain);
				}
			}

			boolean infect = source.hasAbility(Infect.class);
			if(!infect)
				infect = fakeAbilityForDamage(batch, Infect.class, game.actualState);
			if(taker.isPlayer())
			{
				Player losingLife = (Player)taker;
				if(infect)
				{
					if(!playersGettingPoisonCounters.containsKey(losingLife))
						playersGettingPoisonCounters.put(losingLife, 1);
					else
						// can't ++ an Integer
						playersGettingPoisonCounters.put(losingLife, playersGettingPoisonCounters.get(losingLife) + 1);
				}
				else
				{
					if(lifeLosses.containsKey(losingLife))
					{
						Map<GameObject, Integer> lifeLoss = lifeLosses.get(losingLife);
						if(lifeLoss.containsKey(source))
							lifeLoss.put(source, lifeLoss.get(source) + 1);
						else
							lifeLoss.put(source, 1);
					}
					else
					{
						Map<GameObject, Integer> lifeLoss = new HashMap<GameObject, Integer>();
						lifeLoss.put(source, 1);
						lifeLosses.put(losingLife, lifeLoss);
					}
				}

				continue;
			}

			// if it's not an object, we'll get a class-cast exception here
			GameObject takerObject = (GameObject)taker;
			if(takerObject.getTypes().contains(Type.CREATURE))
			{
				// If the source has wither/infect add -1/-1 counters,
				// otherwise, increment damage
				boolean wither = source.hasAbility(Wither.class);
				if(!wither)
					wither = fakeAbilityForDamage(batch, Wither.class, game.actualState);
				if(wither || infect)
				{
					if(!creaturesGettingCounters.containsKey(takerObject))
						creaturesGettingCounters.put(takerObject, 1);
					else
						// can't ++ an Integer
						creaturesGettingCounters.put(takerObject, creaturesGettingCounters.get(takerObject) + 1);
				}
				else
				{
					// Mark any creature damaged by deathtouch so SBAs can
					// destroy them
					GameObject physical = takerObject.getPhysical();
					if(source.hasAbility(Deathtouch.class))
						physical.setDamagedByDeathtouchSinceLastSBA(true);
					physical.setDamage(physical.getDamage() + 1);
				}
			}
			if(takerObject.getTypes().contains(Type.PLANESWALKER))
			{
				if(!planeswalkersLosingCounters.containsKey(takerObject))
					planeswalkersLosingCounters.put(takerObject, 1);
				else
					// can't ++ an Integer
					planeswalkersLosingCounters.put(takerObject, planeswalkersLosingCounters.get(takerObject) + 1);
			}
		}

		for(Map.Entry<Player, Integer> playerPoisonCounter: playersGettingPoisonCounters.entrySet())
		{
			Player player = playerPoisonCounter.getKey();
			int number = playerPoisonCounter.getValue();

			Map<Parameter, MagicSet> witherParameters = new HashMap<Parameter, MagicSet>();
			witherParameters.put(Parameter.PLAYER, new MagicSet(player));
			witherParameters.put(Parameter.NUMBER, new MagicSet(number));
			createEvent(game, "Put " + number + " poison counter" + (number == 1 ? "" : "s") + " on " + player + ".", ADD_POISON_COUNTERS, witherParameters).perform(event, false);
		}

		for(Map.Entry<Player, Map<GameObject, Integer>> playerLifeGain: lifeGains.entrySet())
		{
			Player player = playerLifeGain.getKey();
			for(Map.Entry<GameObject, Integer> lifeGain: playerLifeGain.getValue().entrySet())
			{
				Map<Parameter, MagicSet> gainLifeParameters = new HashMap<Parameter, MagicSet>();
				gainLifeParameters.put(Parameter.CAUSE, new MagicSet(lifeGain.getKey()));
				gainLifeParameters.put(Parameter.PLAYER, new MagicSet(player));
				gainLifeParameters.put(Parameter.NUMBER, new MagicSet(lifeGain.getValue()));
				createEvent(game, player + " gains " + lifeGain.getValue() + " life.", GAIN_LIFE, gainLifeParameters).perform(event, false);
			}
		}

		for(Map.Entry<Player, Map<GameObject, Integer>> playerLifeLoss: lifeLosses.entrySet())
		{
			Player player = playerLifeLoss.getKey();
			for(Map.Entry<GameObject, Integer> lifeLoss: playerLifeLoss.getValue().entrySet())
			{
				Map<Parameter, MagicSet> loseLifeParameters = new HashMap<Parameter, MagicSet>();
				loseLifeParameters.put(Parameter.CAUSE, new MagicSet(lifeLoss.getKey()));
				loseLifeParameters.put(Parameter.PLAYER, new MagicSet(player));
				loseLifeParameters.put(Parameter.NUMBER, new MagicSet(lifeLoss.getValue()));
				loseLifeParameters.put(Parameter.DAMAGE, Empty.set);
				createEvent(game, player + " loses " + lifeLoss.getValue() + " life.", LOSE_LIFE, loseLifeParameters).perform(event, false);
			}
		}

		// same for creatures and -1/-1 counters
		for(Map.Entry<GameObject, Integer> witherCounter: creaturesGettingCounters.entrySet())
		{
			GameObject taker = witherCounter.getKey();
			int number = witherCounter.getValue();

			Map<Parameter, MagicSet> witherParameters = new HashMap<Parameter, MagicSet>();
			witherParameters.put(Parameter.CAUSE, new MagicSet(game));
			witherParameters.put(Parameter.COUNTER, new MagicSet(Counter.CounterType.MINUS_ONE_MINUS_ONE));
			witherParameters.put(Parameter.NUMBER, new MagicSet(number));
			witherParameters.put(Parameter.OBJECT, new MagicSet(taker));
			createEvent(game, "Put " + number + " -1/-1 counter" + (number == 1 ? "" : "s") + " on " + taker + ".", PUT_COUNTERS, witherParameters).perform(event, false);
		}

		for(Map.Entry<GameObject, Integer> loyaltyCounter: planeswalkersLosingCounters.entrySet())
		{
			GameObject taker = loyaltyCounter.getKey();
			int number = planeswalkersLosingCounters.get(taker);

			Map<Parameter, MagicSet> removeCounterParameters = new HashMap<Parameter, MagicSet>();
			removeCounterParameters.put(Parameter.CAUSE, new MagicSet(game));
			removeCounterParameters.put(Parameter.COUNTER, new MagicSet(Counter.CounterType.LOYALTY));
			removeCounterParameters.put(Parameter.NUMBER, new MagicSet(number));
			removeCounterParameters.put(Parameter.OBJECT, new MagicSet(taker));

			createEvent(game, "Remove " + number + " loyalty counter" + (number == 1 ? "" : "s") + " from " + taker + ".", REMOVE_COUNTERS, removeCounterParameters).perform(event, false);
		}

		// If we get as far as this event type, all the damage here will be
		// dealt. No need to check for damage not being dealt, so we just
		// add the assignments directly to the result. -RulesGuru
		event.setResult(Identity.instance(assignments));

		return true;
	}
}