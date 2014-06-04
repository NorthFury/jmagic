package org.rnd.jmagic.engine;

import java.util.Set;

/**
 * This is a class for matching elements of a Set and returning similarly
 * matched elements grouped together.
 */
public interface GroupingPattern
{
	public Set<MagicSet> match(MagicSet set, Identified thisObject, GameState state);
}
