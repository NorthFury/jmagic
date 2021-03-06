package org.rnd.jmagic.engine.patterns;

import org.rnd.jmagic.engine.*;
import org.rnd.jmagic.engine.DamageAssignment.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Matches damage based solely on a Grouping pattern for the dealers and/or the
 * takers. Non-combat damage can also be filtered out.
 */
public class SimpleDamagePattern implements DamagePattern
{
	private final GroupingPattern dealer;
	private final GroupingPattern taker;
	private final boolean combatDamage;

	public SimpleDamagePattern(SetGenerator dealer, SetGenerator taker)
	{
		this(dealer, taker, false);
	}

	public SimpleDamagePattern(GroupingPattern dealer, GroupingPattern taker)
	{
		this(dealer, taker, false);
	}

	public SimpleDamagePattern(SetGenerator dealer, SetGenerator taker, boolean combatDamage)
	{
		this.dealer = (dealer == null ? null : new SimpleGroupingPattern(dealer));
		this.taker = (taker == null ? null : new SimpleGroupingPattern(taker));
		this.combatDamage = combatDamage;
	}

	public SimpleDamagePattern(GroupingPattern dealer, GroupingPattern taker, boolean combatDamage)
	{
		this.dealer = dealer;
		this.taker = taker;
		this.combatDamage = combatDamage;
	}

	@Override
	public Set<Batch> match(Batch batch, Identified thisObject, GameState state)
	{
		MagicSet dealers = new MagicSet();
		MagicSet takers = new MagicSet();

		Iterator<DamageAssignment> iter = batch.iterator();
		while(iter.hasNext())
		{
			DamageAssignment damage = iter.next();

			if(this.combatDamage && !damage.isCombatDamage)
			{
				iter.remove();
				continue;
			}

			dealers.add(state.get(damage.sourceID));
			takers.add(state.get(damage.takerID));
		}

		Set<Batch> ret = new HashSet<Batch>();

		if(this.dealer != null)
		{
			for(MagicSet set: this.dealer.match(dealers, thisObject, state))
			{
				Batch dealerBatch = new Batch();

				for(Identified dealer: set.getAll(Identified.class))
					for(DamageAssignment damage: batch)
						if(damage.sourceID == dealer.ID)
							dealerBatch.add(damage);

				if(!dealerBatch.isEmpty())
					ret.add(dealerBatch);
			}
		}
		else
			ret.add(new Batch(batch));

		if(this.taker != null)
		{
			Set<Batch> newRet = new HashSet<Batch>();

			for(Batch dealerBatch: ret)
			{
				for(MagicSet set: this.taker.match(takers, thisObject, state))
				{
					Batch takerBatch = new Batch();

					for(Identified taker: set.getAll(Identified.class))
						for(DamageAssignment damage: dealerBatch)
							if(damage.takerID == taker.ID)
								takerBatch.add(damage);

					if(!takerBatch.isEmpty())
						newRet.add(takerBatch);
				}
			}

			ret = newRet;
		}

		return ret;
	}
}
