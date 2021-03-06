package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;
import org.rnd.jmagic.engine.*;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Name("Fire Servant")
@Types({Type.CREATURE})
@SubTypes({SubType.ELEMENTAL})
@ManaCost("3RR")
@Printings({@Printings.Printed(ex = Expansion.MAGIC_2011, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.RED})
public final class FireServant extends Card
{
	public static final class DoubleDamageEffect extends DamageReplacementEffect
	{
		public DoubleDamageEffect(Game game)
		{
			super(game, "If a red instant or sorcery spell you control would deal damage, it deals double that damage instead.");
		}

		@Override
		public DamageAssignment.Batch match(Event context, DamageAssignment.Batch damageAssignments)
		{
			DamageAssignment.Batch batch = new DamageAssignment.Batch();

			for(DamageAssignment assignment: damageAssignments)
			{
				GameObject source = context.state.get(assignment.sourceID);
				if(!source.getColors().contains(Color.RED))
					continue;
				if(!source.getTypes().contains(Type.INSTANT) && !source.getTypes().contains(Type.SORCERY))
					continue;
				batch.add(assignment);
			}

			return batch;
		}

		@Override
		public List<EventFactory> replace(DamageAssignment.Batch damageAssignments)
		{
			Collection<DamageAssignment> duplicates = new LinkedList<DamageAssignment>();
			for(DamageAssignment assignment: damageAssignments)
				duplicates.add(new DamageAssignment(assignment));
			damageAssignments.addAll(duplicates);

			return new LinkedList<EventFactory>();
		}
	}

	public static final class FireServantAbility0 extends StaticAbility
	{
		public FireServantAbility0(GameState state)
		{
			super(state, "If a red instant or sorcery spell you control would deal damage, it deals double that damage instead.");

			this.addEffectPart(replacementEffectPart(new DoubleDamageEffect(this.game)));
		}
	}

	public FireServant(GameState state)
	{
		super(state);

		this.setPower(4);
		this.setToughness(3);

		// If a red instant or sorcery spell you control would deal damage, it
		// deals double that damage instead.
		this.addAbility(new FireServantAbility0(state));
	}
}
