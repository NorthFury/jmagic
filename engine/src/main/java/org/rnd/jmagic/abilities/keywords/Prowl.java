package org.rnd.jmagic.abilities.keywords;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Prowl extends Keyword
{
	private final String costString;
	public static final String COST_TYPE = "Prowl";

	public Prowl(GameState state, String costString)
	{
		super(state, "Prowl " + costString);

		this.costString = costString;
	}

	@Override
	public Prowl create(Game game)
	{
		return new Prowl(game.physicalState, this.costString);
	}

	@Override
	protected List<StaticAbility> createStaticAbilities()
	{
		return Collections.<StaticAbility>singletonList(new ProwlAbility(this.state, this.costString));
	}

	public static final class ProwlAbility extends StaticAbility
	{
		/**
		 * Evaluates to the subtypes of creatures who have dealt combat damage
		 * to a player and, at the time of the damage, were controlled by one of
		 * the given players. The subtypes are reported as what the creature had
		 * at the time of damage, not in the current state.
		 */
		public static class TypesDealtCombatDamageToPlayer extends SetGenerator
		{
			/**
			 * The value of this tracker is a map from a players id to the
			 * subtypes that player controlled that dealt combat damage this
			 * turn.
			 */
			public static class TypesDealingCombatDamageToPlayers extends Tracker<Map<Integer, Set<SubType>>>
			{
				private HashMap<Integer, Set<SubType>> ids = new HashMap<Integer, Set<SubType>>();
				private Map<Integer, Set<SubType>> unmodifiable = Collections.unmodifiableMap(this.ids);

				@SuppressWarnings("unchecked")
				@Override
				public TypesDealingCombatDamageToPlayers clone()
				{
					TypesDealingCombatDamageToPlayers ret = (TypesDealingCombatDamageToPlayers)super.clone();
					ret.ids = (HashMap<Integer, Set<SubType>>)this.ids.clone();
					ret.unmodifiable = Collections.unmodifiableMap(ret.ids);
					return ret;
				}

				@Override
				protected Map<Integer, Set<SubType>> getValueInternal()
				{
					return this.unmodifiable;
				}

				@Override
				protected boolean match(GameState state, Event event)
				{
					return event.type == EventType.DEAL_DAMAGE_BATCHES;
				}

				@Override
				protected void reset()
				{
					this.ids.clear();
				}

				@Override
				protected void update(GameState state, Event event)
				{
					Set<DamageAssignment> assignments = event.parameters.get(EventType.Parameter.TARGET).evaluate(state, null).getAll(DamageAssignment.class);
					for(DamageAssignment assignment: assignments)
						if(state.<Identified>get(assignment.takerID).isPlayer() && assignment.isCombatDamage)
						{
							Set<SubType> controllersTypes;
							GameObject source = state.get(assignment.sourceID);
							if(this.ids.containsKey(source.controllerID))
								controllersTypes = this.ids.get(source.controllerID);
							else
							{
								controllersTypes = EnumSet.noneOf(SubType.class);
								this.ids.put(source.controllerID, controllersTypes);
							}

							controllersTypes.addAll(source.getSubTypes());
						}
				}

			}

			public static TypesDealtCombatDamageToPlayer instance(SetGenerator controller)
			{
				return new TypesDealtCombatDamageToPlayer(controller);
			}

			private SetGenerator controller;

			private TypesDealtCombatDamageToPlayer(SetGenerator controller)
			{
				this.controller = controller;
			}

			@Override
			public MagicSet evaluate(GameState state, Identified thisObject)
			{
				MagicSet ret = new MagicSet();

				Set<Player> controllers = this.controller.evaluate(state, thisObject).getAll(Player.class);
				Map<Integer, Set<SubType>> trackerValue = state.getTracker(TypesDealingCombatDamageToPlayers.class).getValue(state);

				for(Player controller: controllers)
					if(trackerValue.containsKey(controller.ID))
						ret.addAll(trackerValue.get(controller.ID));

				return ret;
			}
		}

		private final String costString;

		public ProwlAbility(GameState state, String costString)
		{
			super(state, "You may pay " + costString + " rather than pay this spell's mana cost if a player was dealt combat damage this turn by a source that, at the time it dealt that damage, was under your control and had any of this spell's creature types.");

			this.costString = costString;

			this.canApply = Intersect.instance(SubTypesOf.instance(This.instance(), Type.CREATURE), TypesDealtCombatDamageToPlayer.instance(You.instance()));

			ContinuousEffect.Part part = new ContinuousEffect.Part(ContinuousEffectType.ALTERNATE_COST);
			part.parameters.put(ContinuousEffectType.Parameter.COST, Identity.instance(new CostCollection(COST_TYPE, costString)));
			part.parameters.put(ContinuousEffectType.Parameter.PLAYER, You.instance());
			part.parameters.put(ContinuousEffectType.Parameter.OBJECT, This.instance());
			this.addEffectPart(part);

			state.ensureTracker(new TypesDealtCombatDamageToPlayer.TypesDealingCombatDamageToPlayers());
		}

		@Override
		public ProwlAbility create(Game game)
		{
			return new ProwlAbility(game.physicalState, this.costString);
		}
	}
}
