package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Name("Luminesce")
@Types({Type.INSTANT})
@ManaCost("W")
@Printings({@Printings.Printed(ex = Expansion.TENTH_EDITION, r = Rarity.UNCOMMON), @Printings.Printed(ex = Expansion.COLDSNAP, r = Rarity.UNCOMMON)})
@ColorIdentity({Color.WHITE})
public final class Luminesce extends Card
{

	public static final class LuminesceEffect extends DamageReplacementEffect
	{
		private SetGenerator colorsToPreventFrom = Identity.instance(Color.BLACK, Color.RED);

		public LuminesceEffect(Game game, String name)
		{
			super(game, name);
			this.makePreventionEffect();
		}

		@Override
		public DamageAssignment.Batch match(Event context, DamageAssignment.Batch damageAssignments)
		{
			DamageAssignment.Batch ret = new DamageAssignment.Batch();

			Set<Color> colors = this.colorsToPreventFrom.evaluate(context.state, this.getSourceObject(context.state)).getAll(Color.class);
			damageLoop: for(DamageAssignment damage: damageAssignments)
				for(Color color: colors)
				{
					if(context.state.<GameObject>get(damage.sourceID).getColors().contains(color))
					{
						ret.add(damage);
						continue damageLoop;
					}
				}
			return ret;
		}

		@Override
		public List<EventFactory> prevent(DamageAssignment.Batch damageAssignments)
		{
			damageAssignments.clear();
			return new LinkedList<EventFactory>();
		}
	}

	public Luminesce(GameState state)
	{
		super(state);

		// Prevent all damage that black sources and red sources would deal this
		// turn.
		DamageReplacementEffect replacementEffect = new LuminesceEffect(state.game, "Prevent all damage that black sources and red sources would deal this turn.");

		this.addEffect(createFloatingReplacement(replacementEffect, "Prevent all damage that black sources and red sources would deal this turn."));
	}
}
