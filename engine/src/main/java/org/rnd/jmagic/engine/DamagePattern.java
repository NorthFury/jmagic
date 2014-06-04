package org.rnd.jmagic.engine;

import java.util.Set;

/**
 * Provides a method for matching and grouping damage into triggerable batches.
 */
public interface DamagePattern
{
	public Set<DamageAssignment.Batch> match(DamageAssignment.Batch damage, Identified thisObject, GameState state);
}
