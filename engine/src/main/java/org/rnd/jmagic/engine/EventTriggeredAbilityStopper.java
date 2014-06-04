package org.rnd.jmagic.engine;

import java.util.HashSet;
import java.util.Set;

/**
 * This class is used by any ability that prevents triggered abilities from
 * triggering. Currently, it only supports matching those triggered abilities by
 * the zone changes they contain.
 */
public class EventTriggeredAbilityStopper
{
	private Set<ZoneChangePattern> zoneChangePatterns;

	public EventTriggeredAbilityStopper()
	{
		this.zoneChangePatterns = new HashSet<ZoneChangePattern>();
	}

	public EventTriggeredAbilityStopper(ZoneChangePattern pattern)
	{
		this();
		this.addPattern(pattern);
	}

	public void addPattern(ZoneChangePattern pattern)
	{
		this.zoneChangePatterns.add(pattern);
	}

	public boolean stops(ZoneChange zc, GameState state)
	{
		for(ZoneChangePattern pattern: this.zoneChangePatterns)
			// For now we can pass null for thisObject, until something
			// comes along that needs it.
			if(pattern.match(zc, null, state))
				return true;

		return false;
	}
}
