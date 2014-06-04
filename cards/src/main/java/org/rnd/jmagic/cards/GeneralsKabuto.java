package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.abilities.keywords.Equip;
import org.rnd.jmagic.abilities.keywords.Shroud;
import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.Collections;
import java.util.List;

@Name("General's Kabuto")
@Types({Type.ARTIFACT})
@SubTypes({SubType.EQUIPMENT})
@ManaCost("4")
@Printings({@Printings.Printed(ex = Expansion.CHAMPIONS_OF_KAMIGAWA, r = Rarity.RARE)})
@ColorIdentity({})
public final class GeneralsKabuto extends Card
{
	public static final class GeneralsKabutoAbility0 extends StaticAbility
	{
		public GeneralsKabutoAbility0(GameState state)
		{
			super(state, "Equipped creature has shroud.");
			this.addEffectPart(addAbilityToObject(EquippedBy.instance(This.instance()), Shroud.class));
		}
	}

	public static final class MCHammer extends StaticAbility
	{
		public static final class MCHammerEffect extends DamageReplacementEffect
		{
			public MCHammerEffect(Game game, String name)
			{
				super(game, name);
			}

			@Override
			public DamageAssignment.Batch match(Event context, DamageAssignment.Batch damageAssignments)
			{
				DamageAssignment.Batch ret = new DamageAssignment.Batch();

				GameObject thisObject = (GameObject)this.getStaticSourceObject(context.game.actualState);
				if(thisObject.getAttachedTo() == -1)
					return ret;
				GameObject equippedCreature = context.game.actualState.get(thisObject.getAttachedTo());

				for(DamageAssignment assignment: damageAssignments)
					if(assignment.isCombatDamage && assignment.takerID == equippedCreature.ID)
						ret.add(assignment);
				return ret;
			}

			@Override
			public List<EventFactory> prevent(DamageAssignment.Batch damageAssignments)
			{
				damageAssignments.clear();
				return Collections.emptyList();
			}
		}

		public MCHammer(GameState state)
		{
			super(state, "Prevent all combat damage that would be dealt to equipped creature.");

			ContinuousEffect.Part part = new ContinuousEffect.Part(ContinuousEffectType.REPLACEMENT_EFFECT);
			part.parameters.put(ContinuousEffectType.Parameter.OBJECT, Identity.instance(new MCHammerEffect(state.game, "Prevent all combat damage that would be dealt to equipped creature")));
			this.addEffectPart(part);
		}
	}

	public GeneralsKabuto(GameState state)
	{
		super(state);

		// Equipped creature has shroud.
		this.addAbility(new GeneralsKabutoAbility0(state));

		// Prevent all combat damage that would be dealt to equipped creature.
		this.addAbility(new MCHammer(state));

		// Equip (2)
		this.addAbility(new Equip(state, "(2)"));
	}
}
