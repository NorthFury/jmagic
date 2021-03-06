package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.LinkedList;
import java.util.List;

@Name("Safe Passage")
@Types({Type.INSTANT})
@ManaCost("2W")
@Printings({@Printings.Printed(ex = Expansion.MAGIC_2013, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.MAGIC_2011, r = Rarity.COMMON), @Printings.Printed(ex = Expansion.MAGIC_2010, r = Rarity.COMMON)})
@ColorIdentity({Color.WHITE})
public final class SafePassage extends Card
{
	public static final class SafePassageReplacement extends DamageReplacementEffect
	{
		public SafePassageReplacement(Game game)
		{
			super(game, "Prevent all damage that would be dealt to you and creatures you control");
			this.makePreventionEffect();
		}

		@Override
		public DamageAssignment.Batch match(Event context, DamageAssignment.Batch damageAssignments)
		{
			DamageAssignment.Batch ret = new DamageAssignment.Batch();

			MagicSet valid = Union.instance(You.instance(), CREATURES_YOU_CONTROL).evaluate(context.state, this.getSourceObject(context.state));

			for(DamageAssignment damage: damageAssignments)
				if(valid.contains(context.state.get(damage.takerID)))
					ret.add(damage);

			return ret;
		}

		@Override
		public List<EventFactory> prevent(DamageAssignment.Batch damageAssignments)
		{
			damageAssignments.clear();
			return new LinkedList<EventFactory>();
		}
	}

	public SafePassage(GameState state)
	{
		super(state);

		DamageReplacementEffect replacement = new SafePassageReplacement(state.game);
		this.addEffect(createFloatingReplacement(replacement, "Prevent all damage that would be dealt to you and creatures you control this turn."));
	}
}
