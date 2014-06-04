package org.rnd.jmagic.engine;

import org.rnd.util.NumberRange;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class BlockingRequirement
{
	private Collection<Integer> attacking;
	private NumberRange blockingRange;
	private Collection<Integer> blocking;

	/**
	 * @param attacking The set of creatures that can be blocked to satisfy this
	 * requirement.
	 * @param blockingRange The number of creatures from the blocking set
	 * allowed to block a creature from the attacking set in order to satisfy
	 * this requirement.
	 * @param blocking The set of creatures that can block to satisfy this
	 * requirement.
	 */
	public BlockingRequirement(Collection<GameObject> attacking, NumberRange blockingRange, Collection<GameObject> blocking)
	{
		this.attacking = new LinkedList<Integer>();
		for(GameObject o: attacking)
			this.attacking.add(o.ID);
		this.blockingRange = blockingRange;
		this.blocking = new LinkedList<Integer>();
		for(GameObject o: blocking)
			this.blocking.add(o.ID);
	}

	/**
	 * "Optimizes" this blocking requirement by removing from the blocking set
	 * all creatures not controlled by the specified player.
	 * 
	 * @param defender The defending player to optimize around
	 * @param state The state in which to evaluate who controls which creatures
	 * @return Whether this requirement still has any blocking creatures after
	 * the optimization
	 */
	public boolean defendingPlayerIs(Player defender, GameState state)
	{
		Iterator<Integer> i = this.blocking.iterator();
		while(i.hasNext())
			if(state.getByIDObject(i.next()).controllerID != defender.ID)
				i.remove();

		return !this.blocking.isEmpty();
	}

	public boolean isSatisfied(GameState state)
	{
		int blocks = 0;
		for(Integer blockerID: this.blocking)
		{
			GameObject blocker = state.get(blockerID);
			for(Integer beingBlockedID: blocker.getBlockingIDs())
				if(this.attacking.contains(beingBlockedID))
					++blocks;
		}
		return this.blockingRange.contains(blocks);
	}
}
