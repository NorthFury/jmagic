package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Name("Divine Deflection")
@Types({Type.INSTANT})
@ManaCost("XW")
@Printings({@Printings.Printed(ex = Expansion.AVACYN_RESTORED, r = Rarity.RARE)})
@ColorIdentity({Color.WHITE})
public final class DivineDeflection extends Card
{
	public static final class DivineDeflectionEffect extends DamageReplacementEffect
	{
		private final int target;

		public DivineDeflectionEffect(Game game, String name, Identified target)
		{
			super(game, name);
			this.target = target.ID;
			this.makePreventionEffect();
		}

		@Override
		public DamageAssignment.Batch match(Event context, DamageAssignment.Batch damageAssignments)
		{
			Player you = ((GameObject)this.getSourceObject(context.game.actualState)).getController(context.game.actualState);
			MagicSet yourPermanents = ControlledBy.instance(Identity.instance(you)).evaluate(context.game, this.getSourceObject(context.game.actualState));

			DamageAssignment.Batch ret = new DamageAssignment.Batch();
			for(DamageAssignment damage: damageAssignments)
			{
				if(you.ID == damage.takerID)
					ret.add(damage);
				else if(yourPermanents.contains(context.state.get(damage.takerID)))
					ret.add(damage);
			}
			return ret;
		}

		@Override
		public List<EventFactory> prevent(DamageAssignment.Batch damageAssignments)
		{
			if(damageAssignments.isEmpty())
				return Collections.emptyList();

			FloatingContinuousEffect fce = this.getFloatingContinuousEffect(this.game.physicalState);
			if(fce.damage == 0)
				return Collections.emptyList();

			// This will never get more damage than it can prevent
			int prevented = damageAssignments.size();
			fce.damage -= prevented;
			damageAssignments.clear();
			return Collections.singletonList(spellDealDamage(prevented, Identity.instance(this.game.actualState.get(this.target)), "Divine Deflection deals that much damage to target creature or player."));
		}

		@Override
		public Collection<GameObject> refersTo(GameState state)
		{
			return Collections.singleton((GameObject)this.getSourceObject(state));
		}
	}

	/**
	 * @eparam CAUSE: divine deflection
	 * @eparam PLAYER: controller of CAUSE
	 * @eparam OBJECT: permanents controlled by PLAYER
	 * @eparam NUMBER: the value of X
	 * @eparam TARGET: creature or player targeted by CAUSE
	 */
	public static final EventType DIVINE_DEFLECTION_EVENT = new EventType("DIVINE_DEFLECTION_EVENT")
	{
		@Override
		public Parameter affects()
		{
			return null;
		}

		@Override
		public boolean perform(Game game, Event event, Map<Parameter, MagicSet> parameters)
		{
			final Identified target = parameters.get(Parameter.TARGET).getOne(Identified.class);

			DamageReplacementEffect replacement = new DivineDeflectionEffect(game, "Prevent the next X damage that would be dealt to you and/or permanents you control this turn. If damage is prevented this way, Divine Deflection deals that much damage to target creature or player.", target);

			ContinuousEffect.Part part = replacementEffectPart(replacement);

			Map<Parameter, MagicSet> fceParameters = new HashMap<Parameter, MagicSet>();
			fceParameters.put(Parameter.CAUSE, parameters.get(Parameter.CAUSE));
			fceParameters.put(Parameter.EFFECT, new MagicSet(part));
			fceParameters.put(Parameter.DAMAGE, new MagicSet(Sum.get(parameters.get(Parameter.NUMBER))));
			Event createFce = createEvent(game, "Prevent the next X damage that would be dealt to you and/or permanents you control this turn. If damage is prevented this way, Divine Deflection deals that much damage to target creature or player.", CREATE_FLOATING_CONTINUOUS_EFFECT, fceParameters);
			createFce.perform(event, true);

			event.setResult(Empty.set);
			return true;
		}
	};

	public DivineDeflection(GameState state)
	{
		super(state);

		// Prevent the next X damage that would be dealt to you and/or
		// permanents you control this turn. If damage is prevented this way,
		// Divine Deflection deals that much damage to target creature or
		// player.
		Target t = this.addTarget(CREATURES_AND_PLAYERS, "target creature or player");

		EventFactory effect = new EventFactory(DIVINE_DEFLECTION_EVENT, "Prevent the next X damage that would be dealt to you and/or permanents you control this turn. If damage is prevented this way, Divine Deflection deals that much damage to target creature or player.");
		effect.parameters.put(EventType.Parameter.CAUSE, This.instance());
		effect.parameters.put(EventType.Parameter.PLAYER, You.instance());
		effect.parameters.put(EventType.Parameter.OBJECT, ControlledBy.instance(You.instance()));
		effect.parameters.put(EventType.Parameter.NUMBER, ValueOfX.instance(This.instance()));
		effect.parameters.put(EventType.Parameter.TARGET, targetedBy(t));
		this.addEffect(effect);
	}
}
