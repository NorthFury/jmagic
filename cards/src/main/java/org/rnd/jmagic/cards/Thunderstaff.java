package org.rnd.jmagic.cards;

import static org.rnd.jmagic.Convenience.*;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.generators.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Name("Thunderstaff")
@Types({Type.ARTIFACT})
@ManaCost("3")
@Printings({@Printings.Printed(ex = Expansion.DARKSTEEL, r = Rarity.UNCOMMON)})
@ColorIdentity({})
public final class Thunderstaff extends Card
{
	public static final class PreventSomeDamage extends StaticAbility
	{
		private static final class ThunderCatsHo extends DamageReplacementEffect
		{
			private ThunderCatsHo(Game game, String name)
			{
				super(game, name);

				this.makePreventionEffect();
			}

			@Override
			public DamageAssignment.Batch match(Event context, DamageAssignment.Batch damageAssignments)
			{
				GameObject thisObject = (GameObject)this.getStaticSourceObject(context.game.actualState);
				Player you = thisObject.getController(thisObject.state);

				DamageAssignment.Batch ret = new DamageAssignment.Batch();
				for(DamageAssignment damage: damageAssignments)
				{
					if(!damage.isCombatDamage)
						continue;
					if(damage.takerID == you.ID)
						ret.add(damage);
				}
				return ret;
			}

			@Override
			public List<EventFactory> prevent(DamageAssignment.Batch damageAssignments)
			{
				Set<Integer> preventedFrom = new HashSet<Integer>();
				Iterator<DamageAssignment> damageIter = damageAssignments.iterator();
				while(damageIter.hasNext())
				{
					DamageAssignment damage = damageIter.next();
					if(!preventedFrom.contains(damage.sourceID))
					{
						damageIter.remove();
						preventedFrom.add(damage.sourceID);
					}
				}
				return new LinkedList<EventFactory>();
			}
		}

		public PreventSomeDamage(GameState state)
		{
			super(state, "As long as Thunderstaff is untapped, if a creature would deal combat damage to you, prevent 1 of that damage.");
			this.canApply = Both.instance(this.canApply, Intersect.instance(This.instance(), Untapped.instance()));

			DamageReplacementEffect replacement = new ThunderCatsHo(state.game, "If a creature would deal combat damage to you, prevent 1 of that damage");

			this.addEffectPart(replacementEffectPart(replacement));
		}
	}

	public static final class ThunderstaffAbility1 extends ActivatedAbility
	{
		public ThunderstaffAbility1(GameState state)
		{
			super(state, "(2), (T): Attacking creatures get +1/+0 until end of turn.");
			this.setManaCost(new ManaPool("(2)"));
			this.costsTap = true;

			this.addEffect(ptChangeUntilEndOfTurn(Attacking.instance(), +1, +0, "Attacking creatures get +1/+0 until end of turn."));
		}
	}

	public Thunderstaff(GameState state)
	{
		super(state);

		// If Thunderstaff is untapped and a creature would deal combat damage
		// to you, prevent 1 of that damage.
		this.addAbility(new PreventSomeDamage(state));

		// (2), (T): Attacking creatures get +1/+0 until end of turn.
		this.addAbility(new ThunderstaffAbility1(state));
	}
}
